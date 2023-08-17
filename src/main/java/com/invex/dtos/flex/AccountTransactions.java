package com.invex.dtos.flex;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactions implements Serializable{
    private static final long serialVersionUID = 1L;
    private String date;
    private String operationType;
    private String account;
    private String description;
    private String operationAmount;  
    private String currentBalance;
    private String module;
    private String beneficiaryBank;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String payerBank;
    private String payerName;
    private String payerAccount;
    private String paymentSubject;
    private String traceNumber;
    private String referenceNumber;
    private String liquidationDateTime;
    private String acceptanceDateTime;
    private String contractReferenceNumber;
    private String accountingReference;
    
    
}
