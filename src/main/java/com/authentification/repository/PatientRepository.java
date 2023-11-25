package com.authentification.repository;

import java.util.Optional;

import com.authentification.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
	Optional<Patient> findByUsername(String username);

	@Query("SELECT u FROM Patient u WHERE u.email = :email")
	Optional<Patient> findByEmail(@Param("email") String email);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}
