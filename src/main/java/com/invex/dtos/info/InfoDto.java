package com.invex.dtos.info;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoDto implements Serializable{
    private static final long serialVersionUID = 1L;

       
    private String transactionID = UUID.randomUUID().toString();
    
    //{200,400,500}
    private int httpStatus;
    
    private String timestamp = LocalDateTime.now().toString();
    
    private String message;
    //Objeto de Respuesta
    private Map<?,?> data;

    public void setData(Object a){
        this.data = convertObjectToMap(a);
    }
    @SuppressWarnings("unchecked")
	public static Map<String, Object> convertObjectToMap(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(object, Map.class);
    }
}