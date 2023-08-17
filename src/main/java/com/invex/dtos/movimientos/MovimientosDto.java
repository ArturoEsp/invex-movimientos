package com.invex.dtos.movimientos;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientosDto implements Serializable{

	private static final long serialVersionUID = 1L;


	private String concepto;//paymentSubject (Asunto de Pago)
	private String fecha;//date
	private String movimiento;//operationAmount
	private String saldo;//currentBalance (Saldo)
	private String referencia;//referenceNumber
	private String fechaAceptacion;//acceptanceDateTime
	private String fechaLiquidacion;//liquidationDateTime
	private String emisor;//payerBank
	private String cuentaOrdenante;//payerAccount
	private String nombreOrdenante;//payerName
	private String receptor;//beneficiaryBank
	private String cuentaBeneficiario;//beneficiaryAccount
	private String nombreBeneficiario;//beneficiaryName
	private String claveRastreo;//traceNumber (Clave de Rastreo)
	private String tipoOperacion;//operationType (A = Todos,C = Depósitos,D = Retiros)
	

	
}
