package com.invex.dtos.flex;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionsResp implements Serializable{
    private static final long serialVersionUID = 1L;
	private List<AccountTransactions> acountTransactions;

}
