package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "adoptante")
public class Adoptante implements UserDetails {

    public enum Genero {
        M, F, O
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adoptante")
    private Long idAdoptante;

    @Column(name = "nombres", length = 50)
    private String nombres;

    @Column(name = "apellidos", length = 50)
    private String apellidos;

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "telefono", length = 9)
    private String telefono;

    @Email
    @Column(name = "email")
    private String email;

    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(name = "usuario", length = 50)
    private String usuario;

    @Column(name = "contrasena", length = 128)
    private String contrasena;

    @Column(name = "fecha_registro")
    private Date fechaRegistro;

    @Column(name = "activo")
    private Boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Genero genero;

    @Column(name = "edad")
    private Integer edad;

    // Método para calcular la edad basada en la fecha de nacimiento
    public Integer edad() {
        if (fechaNacimiento == null) {
            return null; // Si no hay fecha de nacimiento, retornamos null
        }
        LocalDate birthDate = new java.sql.Date(fechaNacimiento.getTime()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);
        return period.getYears(); // Devuelve la edad calculada en años
    }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    // Implementación de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Si no usas roles o permisos, puedes devolver una lista vacía
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return email; // En este caso, el correo electrónico es el nombre de usuario
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Si no tienes expiración, retorna true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Si no tienes bloqueo de cuenta, retorna true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Si las credenciales no expiran, retorna true
    }

    @Override
    public boolean isEnabled() {
        return activo != null && activo; // Si el usuario está activo, devuelve true
    }
}
