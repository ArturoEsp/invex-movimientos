package com.invex.controllers.info;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invex.dtos.info.InfoDto;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/Micro")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InfoController {
    
	@Inject
    @ConfigProperty(name = "quarkus.application.version")
    private String version;

    @Inject
    @ConfigProperty(name = "quarkus.application.name")
    private String name;

    @Inject
    @ConfigProperty(name = "environment.info")
    private String environment;

    @GET
    @Path("/info")
    public Response getInfo(){

        Map<String, String> data = new HashMap<>();
        data.put("Environment", environment);
        data.put("Microservice", name);
        data.put("Version", version);

        InfoDto infodto = new InfoDto();
        infodto.setHttpStatus(200);
        infodto.setMessage("Microservice working fine");
        infodto.setData(data);
        return Response.status(infodto.getHttpStatus()).entity(infodto).build();
   }

    public static String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}

