package ru.deyev.credit.deal.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Application {

    @Id
    private Long id;

//    @Column
//    private ClientDTO client;
//
//    @Column
//    private CreditDTO credit;

//    @Column
//    private ApplicationStatus status;

    @Column
    private Date creationDate;

    @Column
    private Date signDate;

    @Column
    private Integer sesCode;

//    @Column
//    private List<ApplicationStatusHistoryDTO> statusHistory = new ArrayList<ApplicationStatusHistoryDTO>();
}
