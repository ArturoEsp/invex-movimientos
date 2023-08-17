package com.invex.controllers.movimientos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invex.dtos.flex.AccountTransactions;
import com.invex.dtos.flex.ResponseFlex;
import com.invex.dtos.info.InfoDto;
import com.invex.dtos.movimientos.MovimientosDto;
import com.invex.dtos.movimientos.ResponseMovimientosDto;
import com.invex.services.CuentaService;
import com.invex.services.CuentaServiceImpl;
import com.invex.utilities.Utility;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import io.smallrye.jwt.build.Jwt;

@Path("/efectivo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovimientosController {

    private static final Logger LOG = Logger.getLogger(MovimientosController.class.getName());
    
    @Inject
    CuentaService cuentaService;
    
    @Inject
    Utility utility;

	@Inject
    @ConfigProperty(name = "quarkus.application.version")
    private String version;


	
    @GET
    @Path("/generatetoken")
    public String getToken() {
    	// Tiempo de expiración: 20 minutos desde ahora (en milisegundos)
    	long expirationTimeInMillis = System.currentTimeMillis() + (20 * 60 * 1000);

        String token =
                Jwt.issuer("https://example.com/issuer")
                        .upn("jdoe@quarkus.io")
                        .groups(new HashSet<>(Arrays.asList("User", "Admin")))
                        .claim(Claims.birthdate.name(), "2001-07-13")
                        .claim("cui", "123456")
                        .claim("folio", "1234")
                        .expiresAt(expirationTimeInMillis) // Establecer la fecha de expiración
                        .sign();
        System.out.println(token);
        return token;
    }
    @GET
    @Path("/movimientos/{cuenta}/{fecha}/{cep}/{operationType}")
    public Response getMovimientosV2(@PathParam("cuenta") @NotNull String cuenta, @PathParam("fecha") @NotNull String fecha,@PathParam("cep") @NotNull String cep,@PathParam("operationType") @NotNull String operationType, @HeaderParam("Authorization") String authorizationHeader) {
        String response=null;
        InfoDto infodto = new InfoDto();
        
        
        LOG.info("Inicio de consulta movimientos Ws: "+infodto.getTransactionID());
        
        try {
            // Validar que los parámetros no sean nulos o vacíos
        	if (cuenta == null || cuenta.isEmpty()  || cuenta.isBlank() || fecha == null || fecha.isEmpty() || fecha.isBlank()
            		|| cep == null || cep.isEmpty() || cep.isBlank()
            		|| operationType == null || operationType.isEmpty() || operationType.isBlank()
            		) {
        		LOG.severe("Los parámetros cuenta, fecha, cep o operationType son requeridos, id de trasaccion: "+infodto.getTransactionID());
                infodto.setHttpStatus(400);
                infodto.setMessage("Los parámetros cuenta y fecha son requeridos");
                infodto.setData(null);
                return Response.status(Response.Status.BAD_REQUEST).entity(infodto).build();
            }

            // Verificar si el encabezado de autorización es nulo o está vacío
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                LOG.severe("Se requiere el encabezado de autorización: "+infodto.getTransactionID());
                infodto.setHttpStatus(400);
                infodto.setMessage("Se requiere el encabezado de autorización");
                infodto.setData(null);
                return Response.status(Response.Status.BAD_REQUEST).entity(infodto).build();
            }else{
                LOG.info("El encabezado de autorización es: "+authorizationHeader);
            }


            // Verificar y obtener el "cui" del JWT
            String cui = "123456";//utility.verifyAndGetCui(authorizationHeader);
            LOG.info("getMovimientosV2 CUI: "+cui);
            // Verificar y obtener el "cui" del JWT
            String folio = "1234";//utility.verifyAndGetFolio(authorizationHeader);
            LOG.info("getMovimientosV2 folio: "+folio);


            //Consumo del EndPoint del sp
            String numerocuenta = cuentaService.obtenerNumeroCuenta(cuenta,cui,folio);//SP ConsultaCuentasPorCUI
            LOG.info("getMovimientosV2 numerocuenta: "+numerocuenta);//quitar en produccion
            if (numerocuenta == null) {
                LOG.severe("No se pudo obtener el numero de cuenta... cuentaService.obtenerNumeroCuenta");
                infodto.setHttpStatus(400);
                infodto.setMessage("No se pudo obtener el numero de cuenta");
                infodto.setData(null);
                return Response.status(Response.Status.BAD_REQUEST).entity(infodto).build();
            }

            //EndPoint que trae los datos flex
            LOG.info("obtenerMovimientosDesdeEndpoint numerocuenta: "+numerocuenta);//quitar en produccion
            LOG.info("obtenerMovimientosDesdeEndpoint fecha: "+fecha);//quitar en produccion
            LOG.info("obtenerMovimientosDesdeEndpoint operationType: "+operationType);//quitar en produccion
            LOG.info("obtenerMovimientosDesdeEndpoint cep: "+cep);//quitar en produccion
            response = cuentaService.obtenerMovimientosDesdeEndpoint(numerocuenta, fecha,operationType,cep);

            // Convertir la respuesta JSON a una lista de objetos MovimientosDto
            ObjectMapper objectMapper = new ObjectMapper();

            //Descomentar para dejar de mockear y comentar el mockeo de respuesta
            ResponseFlex resp = objectMapper.readValue(response, new TypeReference<ResponseFlex>() {});
            List<AccountTransactions> AccountTransactionsList = resp.getCanonicalInvexMessage().getBody().getAccountTransactionsRes().getAcountTransactions();
            List<MovimientosDto> resultList =  new ArrayList<>();
            ResponseMovimientosDto responseFinal = new ResponseMovimientosDto();
            
            for (AccountTransactions accountTransactions:AccountTransactionsList ){
                MovimientosDto result = new MovimientosDto();
                
                result.setConcepto(accountTransactions.getPaymentSubject());
                result.setFecha(accountTransactions.getDate());
                result.setMovimiento(accountTransactions.getOperationAmount());  
                result.setSaldo(accountTransactions.getCurrentBalance());
                result.setReferencia(String.valueOf(accountTransactions.getReferenceNumber()));
                result.setFechaAceptacion(accountTransactions.getAcceptanceDateTime());
                result.setFechaLiquidacion(accountTransactions.getLiquidationDateTime());
                result.setEmisor(accountTransactions.getPayerBank());
                result.setCuentaOrdenante(accountTransactions.getPayerAccount());
                result.setNombreOrdenante(accountTransactions.getPayerName());
                result.setReceptor(accountTransactions.getBeneficiaryBank());
                result.setCuentaBeneficiario(accountTransactions.getBeneficiaryAccount());
                result.setNombreBeneficiario(accountTransactions.getBeneficiaryName());
                result.setClaveRastreo(accountTransactions.getTraceNumber());
                result.setTipoOperacion(accountTransactions.getOperationType());
                                          
                resultList.add(result);
            }
            responseFinal.setMovimientos(resultList);
           
            
            infodto.setHttpStatus(200);
            infodto.setMessage("Microservice working fine");
            infodto.setData(responseFinal);
            LOG.info("Fin de consulta movimientos Ws: "+infodto.getTransactionID());
            return Response.ok(infodto).build();

        } catch (Exception e) {
            LOG.severe("Error al consultar la informacion del cliente: " + e.getMessage()+", uuid: "+infodto.getTransactionID());
            e.printStackTrace();
            infodto.setHttpStatus(500);
            infodto.setMessage("Error al consultar la información del cliente");
            infodto.setData(null);
            return Response.status(Response.Status.BAD_REQUEST).entity(infodto).build();
        }
    }







   



}
