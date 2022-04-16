package ru.deyev.credit.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.deyev.credit.conveyor.model.Credit;
import ru.deyev.credit.conveyor.model.PaymentScheduleElement;
import ru.deyev.credit.conveyor.model.ScoringData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScoringService {

    private static final String FUNDING_RATE = "15.00";
    private static final String INSURANCE_DISCOUNT = "4.00";
    private static final String SALARY_CLIENT_DISCOUNT = "1.00";
    private static final String INSURANCE_BASE_PRICE = "10000.00";
    private static final String BASE_LOAN_AMOUNT = "200000.00";
    private static final String INSURANCE_LOAN_AMOUNT_MULTIPLICAND = "0.05";
    private static final Integer BASE_PERIODS_AMOUNT_IN_YEAR = 12;
    private static final Integer DEFAULT_DECIMAL_SCALE = 2;


    public Credit calculateCredit(ScoringData scoringData) {

        BigDecimal totalAmount = evaluateTotalAmountByServices(new BigDecimal(scoringData.getAmount()),
                scoringData.getIsInsuranceEnabled());

        BigDecimal requestedAmount = BigDecimal.valueOf(scoringData.getAmount());

        BigDecimal rate = calculateRate(scoringData.getIsInsuranceEnabled(), scoringData.getIsSalaryClient());

        Integer term = scoringData.getTerm();

        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, term, rate);

        List<PaymentScheduleElement> paymentSchedule = calculatePaymentSchedule(totalAmount, scoringData.getTerm(), rate, monthlyPayment);


        return new Credit()
                .amount(totalAmount)
                .monthlyPayment(calculateMonthlyPayment(totalAmount, term, rate))
                .psk(calculatePSK(paymentSchedule, requestedAmount, term))
                .paymentSchedule(paymentSchedule)
                .term(term)
                .rate(rate)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient());
    }

    public BigDecimal calculateRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal rate = new BigDecimal(FUNDING_RATE);

        if (isInsuranceEnabled) {
            rate = rate.subtract(new BigDecimal(INSURANCE_DISCOUNT));
        }
        if (isSalaryClient) {
            rate = rate.subtract(new BigDecimal(SALARY_CLIENT_DISCOUNT));
        }

        return rate;
    }

    public BigDecimal evaluateTotalAmountByServices(BigDecimal amount, Boolean isInsuranceEnabled) {
        if (isInsuranceEnabled) {
            BigDecimal insurancePrice = new BigDecimal(INSURANCE_BASE_PRICE);
            if (amount.compareTo(new BigDecimal(BASE_LOAN_AMOUNT)) > 0) {
                insurancePrice = insurancePrice
                        .add(amount
                                .multiply(new BigDecimal(INSURANCE_LOAN_AMOUNT_MULTIPLICAND)));
            }
            return amount.add(insurancePrice);
        } else {
            return amount;
        }
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate) {
//        Формула расчета аннуитетного платежа:
//         ЕП = СК * КА, где
//          СК - сумма кредита
//            КА - коэффициент аннуитета
//
//            КА = (МП * К)/(К-1), где
//              МП - месячная процентная ставка
//              К = (1 + МП)^КП, где
//                КП - количество платежей
        log.info("Calculating monthly payment -----------------------");
        log.info("totalAmount = {}, term = {}, rate = {}", totalAmount, term, rate);

        BigDecimal monthlyRateAbsolute = rate.divide(BigDecimal.valueOf(100), 5, RoundingMode.CEILING);
        log.info("monthlyRateAbsolute = {}", monthlyRateAbsolute);

        BigDecimal monthlyRate = monthlyRateAbsolute.divide(new BigDecimal(BASE_PERIODS_AMOUNT_IN_YEAR), 6, RoundingMode.CEILING);
        log.info("monthlyRate = {}", monthlyRate);

        BigDecimal intermediateCoefficient = (BigDecimal.ONE.add(monthlyRate)).pow(term)
                .setScale(5, RoundingMode.CEILING);
        log.info("intermediateCoefficient = {}", intermediateCoefficient);

        BigDecimal annuityCoefficient = monthlyRate.multiply(intermediateCoefficient)
                .divide(intermediateCoefficient.subtract(BigDecimal.ONE), RoundingMode.CEILING);
        log.info("annuityCoefficient = {}", annuityCoefficient);

        BigDecimal monthlyPayment = totalAmount.multiply(annuityCoefficient).setScale(2, RoundingMode.CEILING);
        log.info("monthlyPayment = {}", monthlyPayment);
        log.info("End calculating monthly payment -------------------");
        return monthlyPayment;
    }

    public BigDecimal calculatePSK(List<PaymentScheduleElement> paymentSchedule,
                                   BigDecimal requestedAmount, Integer term) {
//        Формула расчета ПСК:
//
//        (СВ/СЗ - 1)/Г * 100, где
//
//          СВ — сумма всех выплат;
//
//          СЗ — сумма займа;
//
//          Г — срок кредитования в годах;

        log.info("Calculating PSK ----------------------------");
        BigDecimal paymentAmount = paymentSchedule
                .stream()
                .map(PaymentScheduleElement::getTotalPayment)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        log.info("paymentAmount = {}", paymentAmount);


        BigDecimal termInYears = divideWithScaleAndRoundingMode(BigDecimal.valueOf(term),
                new BigDecimal(BASE_PERIODS_AMOUNT_IN_YEAR));
        log.info("termInYears = {}", termInYears);

        BigDecimal intermediateCoefficient = divideWithScaleAndRoundingMode(paymentAmount, requestedAmount)
                .subtract(BigDecimal.ONE);
        log.info("intermediateCoefficient = {}", intermediateCoefficient);

        BigDecimal psk = intermediateCoefficient.divide(termInYears, 3, RoundingMode.CEILING)
                .multiply(BigDecimal.valueOf(100));
        log.info("psk = {}", psk);
        log.info("End calculating PSK ------------------------");

        return psk;
    }

    public List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal totalAmount, Integer term,
                                                                 BigDecimal rate, BigDecimal monthlyPayment) {
        BigDecimal remainingDebt = totalAmount.setScale(2, RoundingMode.CEILING);
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();
        LocalDate paymentDate = LocalDate.now();

//      Add initial payment to payment schedule
        paymentSchedule.add(new PaymentScheduleElement()
                .number(0)
                .date(paymentDate)
                .totalPayment(BigDecimal.ZERO)
                .remainingDebt(remainingDebt)
                .interestPayment(BigDecimal.ZERO)
                .debtPayment(BigDecimal.ZERO));

        for (int i = 1; i < term + 1; i++) {
            paymentDate = paymentDate.plusMonths(1);

            BigDecimal interestPayment = calculateInterest(remainingDebt, rate).setScale(2, RoundingMode.CEILING);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);

            remainingDebt = remainingDebt.subtract(debtPayment);

            paymentSchedule.add(new PaymentScheduleElement()
                    .number(i)
                    .date(paymentDate)
                    .totalPayment(monthlyPayment)
                    .remainingDebt(remainingDebt)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment));
        }

        return paymentSchedule;
    }

    public BigDecimal calculateInterest(BigDecimal remainingDebt, BigDecimal rate) {
        BigDecimal monthlyRateAbsolute = rate.divide(BigDecimal.valueOf(100), RoundingMode.CEILING);

        BigDecimal monthlyRate = monthlyRateAbsolute.divide(new BigDecimal(BASE_PERIODS_AMOUNT_IN_YEAR), 10, RoundingMode.CEILING);

        return remainingDebt.multiply(monthlyRate);
    }

    public BigDecimal divideWithScaleAndRoundingMode(BigDecimal number, BigDecimal divisor) {
        return number.divide(divisor, DEFAULT_DECIMAL_SCALE, RoundingMode.CEILING);
    }
}
