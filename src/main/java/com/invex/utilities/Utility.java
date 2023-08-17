package com.invex.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class Utility {
	 // Verificar el Bearer Token y obtener el "cui"
    public String verifyAndGetCui(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());

            try {
                DecodedJWT decodedToken = JWT.decode(token);
                String cui = decodedToken.getClaim("cui").asString();

                return cui;
            } catch (Exception e) {
                // Manejar el error según tus necesidades
                throw new RuntimeException("Error al decodificar el JWT", e);
            }
        }

        throw new WebApplicationException("Bearer token invalido", Response.Status.UNAUTHORIZED);
    }

    // Verificar el Bearer Token y obtener el "cui"
    public String verifyAndGetFolio(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());

            try {
                DecodedJWT decodedToken = JWT.decode(token);
                String folio = decodedToken.getClaim("folio").asString();

                return folio;
            } catch (Exception e) {
                // Manejar el error según tus necesidades
                throw new RuntimeException("Error al decodificar el JWT", e);
            }
        }

        throw new WebApplicationException("Bearer token invalido", Response.Status.UNAUTHORIZED);
    }
}
