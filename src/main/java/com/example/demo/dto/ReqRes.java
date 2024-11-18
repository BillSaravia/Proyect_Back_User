package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.example.demo.entity.Adoptante;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String errorMessage;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;

    @Email(message = "Invalid email format")
    private String email;     // Correo electrónico del usuario

    @NotNull(message = "Usuario is required")
    private String usuario;   // Nombre de usuario

    @NotNull(message = "Nombres are required")
    private String nombres;   // Nombres del adoptante

    @NotNull(message = "Apellidos are required")
    private String apellidos; // Apellidos del adoptante

    @NotNull(message = "Telefono is required")
    private String telefono;  // Teléfono del adoptante

    @NotNull(message = "Contrasena is required")
    private String contrasena;  // Contraseña del adoptante

    private Adoptante adoptante;          // Para manejar un único adoptante
    private List<Adoptante> adoptanteList; // Para manejar una lista de adoptantes


}
