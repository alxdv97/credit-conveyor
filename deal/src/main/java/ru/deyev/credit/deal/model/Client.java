package ru.deyev.credit.deal.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Client {

    @Id
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String middleName;

    @Column
    private String gender;

    @Column
    private java.time.LocalDate birthDate;

    @Column
    private String passportSeries;

    @Column
    private String passportNumber;

    @Column
    private java.time.LocalDate passportIssueDate;

    @Column
    private String passportIssueBranch;

    @Column
    private String maritalStatus;

    @Column
    private Integer dependentAmount;

//    @Column
//    private EmploymentDTO employment;

    @Column
    private String account;
}
