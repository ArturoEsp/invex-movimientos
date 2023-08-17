package com.invex.services;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invex.controllers.movimientos.MovimientosController;
import com.invex.utilities.Constants;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.json.Json;
//import jakarta.persistence.EntityManager;

import jakarta.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Calendar;

import java.util.logging.Logger;


@ApplicationScoped
public class CuentaServiceImpl implements CuentaService{

    private static final Logger LOG = Logger.getLogger(CuentaServiceImpl.class.getName());

    @Inject
    @ConfigProperty(name = "app.API_FLEX_MOVIMIENTOS_URL")
    String API_FLEX_MOVIMIENTOS_URL;
    
    @Inject
    @ConfigProperty(name = "app.API_WSO2_SP_CONSULTACUENTASPORCUI_URL")
    String API_WSO2_SP_CONSULTACUENTASPORCUI_URL;
    
    @Inject
    @ConfigProperty(name = "app.host")
    String host;
    
    @Inject
    @ConfigProperty(name = "app.port")
    int port;
    
    //Metodo que consume el SP ConsultaCuentasPorCUI
    @Transactional
    public String obtenerNumeroCuenta(String cuenta, String cui,String folio) {
        try {
            // Validar que los parámetros no sean nulos o vacíos
            if (cuenta == null || cuenta.isEmpty() || cui == null || cui.isEmpty()) {
                LOG.severe("Los parametros cuenta y cui no pueden ser nulos o vacios");
                throw new IllegalArgumentException("Los parametros cuenta y cui no pueden ser nulos o vacios");
            }


            //Se comento porque se realizo un API Rest para consumir a parte el SP
            LOG.info("obtenerNumeroCuenta cui: " + cui);//Quitar en produccion
           
            String resultado = (String) obtenerConsultacuentasporcuiEndpoint(cui,folio);
            LOG.info("obtenerNumeroCuenta Resultado EndPoint SP: " + resultado);//Quitar en produccion
            // Validar que se obtuvo una respuesta válida del SP
            if (resultado == null || resultado.isEmpty()) {
                LOG.severe("No se obtuvo una respuesta valida del SP ConsultaCuentasPorCUI");
                throw new IllegalStateException("No se obtuvo una respuesta valida del SP ConsultaCuentasPorCUI");
            }

            //Metodo para parsear el resultado y obtener el numerocuenta
            String numerocuenta = parsearNumeroCuenta(resultado, cuenta);
            LOG.info("obtenerNumeroCuenta numerocuenta: " + numerocuenta);//Quitar en produccion
            return numerocuenta;
        } catch (IllegalArgumentException e) {
            LOG.severe("Error: " + e.getMessage());
            return null;
        } catch (IllegalStateException e) {
            LOG.severe("Error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            LOG.severe("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //Metodo para obtener el numero de cuenta del resultado del SP
    public String parsearNumeroCuenta(String respuesta, String cuenta) {
        ObjectMapper mapper = new ObjectMapper();
        String numerocuenta = null;
        LOG.info("parsearNumeroCuenta respuesta: "+respuesta);
        LOG.info("parsearNumeroCuenta cuenta: "+cuenta);
        try {
            JsonNode root = mapper.readTree(respuesta);
            JsonNode respuestaConsultaNode = root.path("result").path("respuestaConsulta");
            String respuestaConsulta = respuestaConsultaNode.asText();

            JsonNode datosNode = mapper.readTree(respuestaConsulta).path("DATOS");
            JsonNode accountNode = mapper.readTree(datosNode.asText()).path("account");

            LOG.info("INICIO parsearNumeroCuenta TRY");
            for (JsonNode cuentaNode : accountNode) {
                int idAccount = cuentaNode.path("idAccount").asInt();
                LOG.info("parsearNumeroCuenta idAccount: " + idAccount);
                String customerAccount = cuentaNode.path("customerAccount").asText();
                LOG.info("parsearNumeroCuenta customerAccount: " + customerAccount);
                if (idAccount == Integer.parseInt(cuenta)) {
                    numerocuenta = customerAccount;
                    break;
                }
            }
            LOG.info("FIN parsearNumeroCuenta TRY");
        } catch (JsonProcessingException e) {
            LOG.severe("Error*************************************: " + e.getMessage());
            e.printStackTrace();
        }

        return numerocuenta;
    }


    //Consumo de enpoint externo flex::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public String obtenerMovimientosDesdeEndpoint(String numerocuenta, String fecha,String operationType, String cep) throws IOException {
        String response = null;

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        java.util.Date currentDate = calendar.getTime();

        // Convertir la fecha actual a java.sql.Date
        java.sql.Date fromDateSql = new java.sql.Date(currentDate.getTime());
        // Convertir la fecha en una representación de cadena
        String fromDateStr = fromDateSql.toString();
        LOG.info("fromDateStr: " + fromDateStr);
        //Variable tipo string request:
        String numerOfTransaccion = "123123"; // no se sabe qué tendrá pero el el endopoint del flex se usa

        // URL del endpoint externo
        String url = API_FLEX_MOVIMIENTOS_URL;

        System.out.println("ENDPOITN FLEX:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::"+API_FLEX_MOVIMIENTOS_URL);
        
  
        // Crear la conexión HTTP
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));//Esta parte sirve para poder llegar a los servicios de flex
        connection = (HttpURLConnection) new URL(url).openConnection(proxy);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        System.out.println("Paso connection contentype");
        
       
        String requestBody = "{" +
                "\"canonicalInvexMessage\": {" +
                "\"header\": {" +
                "\"requestSystem\": \"PORTAL\"" +
                "}," +
                "\"body\": {" +
                "\"accountTransactionsReq\": {" +
                "\"customerAccount\": \"" + numerocuenta + "\"," +
                "\"fromDate\": \"" + fecha + "\"," +
                "\"toDate\": \"" + fromDateStr + "\"," +
                "\"operationType\": \"" + operationType + "\"," +
                "\"CEP\": \"" + cep + "\"" +
                "}" +
                "}" +
                "}" +
                "}";


        // Enviar el cuerpo de la solicitud
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody.getBytes());
        outputStream.flush();

        System.out.println("Paso outputStream");
        // Leer la respuesta del endpoint externo
        int varReposCode = connection.getResponseCode();

        if (varReposCode == HttpURLConnection.HTTP_OK) {
            LOG.info("**************************************EndPoint externo FLEX Tiene status OK");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            response = reader.readLine();
            System.out.println("Que contiene el response: "+response);
            // Cerrar las conexiones
            reader.close();
            connection.disconnect();

        } else {
            LOG.severe("Error Endpoint Flex status : " +varReposCode+" ,Response: "+ response);
            throw new IOException("Status: " + varReposCode);
        }

        return response;
    }


    public String obtenerConsultacuentasporcuiEndpoint(String cui, String folio) throws IOException {
        String response = null;


        // URL del endpoint externo
        String url = API_WSO2_SP_CONSULTACUENTASPORCUI_URL;

        // Crear la conexión HTTP
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();       
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);


        String requestBody = "{ " +
                "\"Request\": {" +
                "\"cui\": \"" + cui + "\"," +
                "\"folio\": \"" + folio + "\"" +
                "}" +
                "}";


        // Enviar el cuerpo de la solicitud
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody.getBytes());
        outputStream.flush();

        // Leer la respuesta del endpoint externo
        int varReposCode = connection.getResponseCode();

        if (varReposCode == HttpURLConnection.HTTP_OK) {
            LOG.info("**************************************EndPoint Externo SP Tiene status OK");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            response = reader.readLine();

            // Cerrar las conexiones
            reader.close();
            connection.disconnect();
        } else {
            LOG.severe("Error Endpoint Externo SP status : " +varReposCode+" ,Response: "+ response);
            throw new IOException("Status: " + varReposCode);
        }

        return response;
    }


}
