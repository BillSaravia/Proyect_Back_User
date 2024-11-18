package com.example.demo.repository;


import com.example.demo.entity.Adoptante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Adoptante, Integer> {

    Optional<Adoptante> findByEmail(String email);
}