package ru.deyev.credit.deal.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class Credit {

    @Id
    private Long id;

    @Column
    private BigDecimal amount;

    @Column
    private Integer term;

    @Column
    private BigDecimal monthlyPayment;

    @Column
    private BigDecimal rate;

    @Column
    private BigDecimal psk;

    @Column
    private Boolean isInsuranceEnabled;

    @Column
    private Boolean isSalaryClient;

//    @Column
//    private List<PaymentScheduleElement> paymentSchedule = null;
}
