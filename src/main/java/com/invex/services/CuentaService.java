package com.invex.services;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;


public interface CuentaService {

	public String obtenerNumeroCuenta(String cuenta, String cui,String folio);
	public String parsearNumeroCuenta(String respuesta, String cuenta);
	public String obtenerMovimientosDesdeEndpoint(String numerocuenta, String fecha,String operationType, String cep) throws IOException;
	public String obtenerConsultacuentasporcuiEndpoint(String cui, String folio) throws IOException;
}
