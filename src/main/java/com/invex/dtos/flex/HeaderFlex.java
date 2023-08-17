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
public class HeaderFlex implements Serializable{
    private static final long serialVersionUID = 1L;
	private String transactionId;
	private String requestSystem;
	private String reqType;
	private String requestDateTime;
	private String responseDateTime;
	private String statusResponse;
	
}
