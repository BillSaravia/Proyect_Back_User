package com.example.demo.service;

import com.example.demo.dto.ReqRes;
import com.example.demo.entity.Adoptante;
import com.example.demo.repository.UsersRepo;
import com.example.demo.service.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersAdoptanteService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register new user
    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            if (registrationRequest == null || registrationRequest.getAdoptante() == null) {
                resp.setStatusCode(400);
                resp.setMessage("Solicitud inválida: faltan datos de adoptante");
                return resp;
            }

            Adoptante adoptante = registrationRequest.getAdoptante();
            if (adoptante.getFechaNacimiento() == null) {
                resp.setStatusCode(400);
                resp.setMessage("La fecha de nacimiento es obligatoria");
                return resp;
            }

            // Aquí se asegura de que los valores sean correctos
            adoptante.setEmail(registrationRequest.getEmail());
            adoptante.setTelefono(registrationRequest.getTelefono());
            adoptante.setUsuario(registrationRequest.getUsuario());
            adoptante.setNombres(registrationRequest.getNombres());
            adoptante.setApellidos(registrationRequest.getApellidos());
            adoptante.setContrasena(passwordEncoder.encode(registrationRequest.getContrasena()));
            adoptante.setFechaNacimiento(adoptante.getFechaNacimiento());
            adoptante.setEdad(adoptante.edad());  // Se calcula la edad automáticamente

            Adoptante savedAdoptante = usersRepo.save(adoptante);
            if (savedAdoptante.getIdAdoptante() > 0) {
                resp.setAdoptante(savedAdoptante);
                resp.setMessage("Adoptante registrado exitosamente");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setErrorMessage("Error al registrar: " + e.getMessage());
        }
        return resp;
    }


    // Login method
    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getContrasena() == null) {
                response.setStatusCode(400);
                response.setMessage("Credenciales no proporcionadas");
                return response;
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getContrasena()));

            Adoptante user = usersRepo.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String jwt = jwtUtils.generateToken((UserDetails) user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), (UserDetails) user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setUsuario(user.getUsuario());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Inicio de sesión exitoso");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error al iniciar sesión: " + e.getMessage());
        }
        return response;
    }

    // Refresh JWT token
    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes response = new ReqRes();
        try {
            if (refreshTokenRequest == null || refreshTokenRequest.getToken() == null) {
                response.setStatusCode(400);
                response.setMessage("Token no proporcionado");
                return response;
            }

            String email = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            Adoptante user = usersRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), (UserDetails) user)) {
                String jwt = jwtUtils.generateToken((UserDetails) user);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Token actualizado correctamente");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error al actualizar el token: " + e.getMessage());
        }
        return response;
    }

    // Get all users
    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();
        try {
            List<Adoptante> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setAdoptanteList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Operación exitosa");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No se encontraron adoptantes");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error al obtener adoptantes: " + e.getMessage());
        }
        return reqRes;
    }

    // Get user by ID
    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Adoptante> adoptante = usersRepo.findById(id);
            if (adoptante.isPresent()) {
                reqRes.setAdoptante(adoptante.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("Adoptante encontrado exitosamente");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Adoptante no encontrado");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error al obtener adoptante: " + e.getMessage());
        }
        return reqRes;
    }

    // Delete user by ID
    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Adoptante> adoptante = usersRepo.findById(userId);
            if (adoptante.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Adoptante eliminado exitosamente");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Adoptante no encontrado");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error al eliminar adoptante: " + e.getMessage());
        }
        return reqRes;
    }

    // Update user information
    public ReqRes updateUser(Integer userId, Adoptante updatedAdoptante) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Adoptante> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                Adoptante existingAdoptante = userOptional.get();
                existingAdoptante.setEmail(updatedAdoptante.getEmail());
                existingAdoptante.setNombres(updatedAdoptante.getNombres());
                existingAdoptante.setApellidos(updatedAdoptante.getApellidos());
                existingAdoptante.setTelefono(updatedAdoptante.getTelefono());

                if (updatedAdoptante.getContrasena() != null && !updatedAdoptante.getContrasena().isEmpty()) {
                    existingAdoptante.setContrasena(passwordEncoder.encode(updatedAdoptante.getContrasena()));
                }

                Adoptante savedAdoptante = usersRepo.save(existingAdoptante);
                reqRes.setAdoptante(savedAdoptante);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Adoptante actualizado exitosamente");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Adoptante no encontrado para actualizar");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error al actualizar adoptante: " + e.getMessage());
        }
        return reqRes;
    }

    // Get user info by email
    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Adoptante> adoptante = usersRepo.findByEmail(email);
            if (adoptante.isPresent()) {
                reqRes.setAdoptante(adoptante.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("Información obtenida exitosamente");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Adoptante no encontrado");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error al obtener información: " + e.getMessage());
        }
        return reqRes;
    }
}
