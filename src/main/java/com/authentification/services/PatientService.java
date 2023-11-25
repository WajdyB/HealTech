package com.authentification.services;

import com.authentification.entities.Patient;
import com.authentification.exceptions.InvalidTokenException;
import com.authentification.exceptions.UserNotFoundException;
import com.authentification.jwt.JwtUtils;
import com.authentification.jwt.Utils;
import com.authentification.payload.MessageResponse;
import com.authentification.payload.PasswordResetToken;
import com.authentification.repository.PasswordResetTokenRepository;
import com.authentification.repository.PatientRepository;
import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id_patient) throws NotFoundException {
        Optional<Patient> OptionalPatient = patientRepository.findById(id_patient);
        if (OptionalPatient.isPresent()) {
            return OptionalPatient.get();
        } else {
            throw new NotFoundException("Patient with id " + id_patient + "not found.");
        }
    }

    public ResponseEntity<Optional<Patient>> getPatientByUsername(String username) {
        Optional<Patient> patient = patientRepository.findByUsername(username);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<MessageResponse> modifyPatient(Long id_patient, Patient newPatient, String token) {

        Optional<Patient> user = patientRepository.findById(id_patient);

        if (user.isPresent()) {
            Optional<Patient> patientToUpdate = patientRepository.findById(id_patient);
            if (patientToUpdate.isPresent()) {
                Patient existingPatient = patientToUpdate.get();

                if (!(newPatient.getUsername().isEmpty())) {
                    existingPatient.setUsername(newPatient.getUsername());
                } else {
                    existingPatient.setUsername(existingPatient.getUsername());
                }
                if (!(newPatient.getFirstname().isEmpty())) {
                    existingPatient.setFirstname(newPatient.getFirstname());
                } else {
                    existingPatient.setFirstname(existingPatient.getFirstname());
                }
                if (!(newPatient.getLastname().isEmpty())) {
                    existingPatient.setLastname(newPatient.getLastname());
                } else {
                    existingPatient.setLastname(existingPatient.getLastname());
                }
                if (!(newPatient.getEmail().isEmpty())) {
                    existingPatient.setEmail(newPatient.getEmail());
                } else {
                    existingPatient.setEmail(existingPatient.getEmail());
                }

                if (newPatient.getPhone() != 0) {
                    existingPatient.setPhone(newPatient.getPhone());
                } else {
                    existingPatient.setPhone(existingPatient.getPhone());
                }

                if (newPatient.getGender() != null) {
                    existingPatient.setGender(newPatient.getGender());
                } else {
                    existingPatient.setGender(existingPatient.getGender());
                }

                if (!(newPatient.getPassword().isEmpty())) {
                    existingPatient.setPassword(encoder.encode(newPatient.getPassword()));
                } else {
                    existingPatient.setPassword(existingPatient.getPassword());
                }

                if (!(newPatient.getAddress().isEmpty())) {
                    existingPatient.setAddress(newPatient.getAddress());
                } else {
                    existingPatient.setAddress(existingPatient.getAddress());
                }

                patientRepository.save(existingPatient);
                return ResponseEntity.ok(new MessageResponse("Patient modified successfully!"));
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Failed to modify patient."));
    }


    public ResponseEntity<MessageResponse> deletePatient(String token) {
        try {
            Long id_patient = jwtUtils.getPatientIdFromToken(token);

            Optional<Patient> patientOptional = patientRepository.findById(id_patient);

            if (patientOptional.isPresent()) {
                Patient existentPatient = patientOptional.get();
                patientRepository.deleteById(id_patient);
                return ResponseEntity.ok(new MessageResponse("Account deleted successfully!"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Patient not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Account not deleted"));
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public void logoutUser(HttpServletRequest request) {
        String token = extractJwtFromRequest(request);
        jwtUtils.invalidateJwtToken(token);
    }


    public String forgotPassword(String email) throws MessagingException, IOException, javax.mail.MessagingException {
        Optional<Patient> patientOptional = patientRepository.findByEmail(email);
        if (patientOptional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        Patient patient = patientOptional.get();

        PasswordResetToken token = passwordResetTokenRepository.findByUserEmail(email);
        if (token != null) {
            passwordResetTokenRepository.delete(token);
        }

        String newToken = Utils.generateRandomToken();
        PasswordResetToken newResetToken = new PasswordResetToken();
        newResetToken.setToken(newToken);
        newResetToken.setUserEmail(email);
        newResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetTokenRepository.save(newResetToken);

        String resetUrl = "http://localhost:3000/resetpassword?token=" + newToken;

        emailService.sendPasswordResetEmail(patient, newResetToken, resetUrl);

        return newToken;
    }


    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        Patient patient = patientRepository.findByEmail(resetToken.getUserEmail()).orElse(null);
        if (patient == null) {
            throw new UserNotFoundException("User not found");
        }

        patient.setPassword(encoder.encode(newPassword));
        patientRepository.save(patient);
        passwordResetTokenRepository.delete(resetToken);
    }


}




