package com.authentification.controllers;

import com.authentification.entities.Patient;
import com.authentification.exceptions.InvalidTokenException;
import com.authentification.exceptions.UserNotFoundException;
import com.authentification.payload.ForgotPasswordResponse;
import com.authentification.payload.MessageResponse;
import com.authentification.services.PatientService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService ;

    @GetMapping("/getAllPatients")
    public List<Patient> getAllPatients() { return patientService.getAllPatients();}

    @GetMapping("/{id_patient}/getPatientById")
    public Patient getPatientById (@PathVariable("id_patient") Long id_patient) throws NotFoundException {
        return patientService.getPatientById(id_patient);
    }

    @GetMapping("/{username}/getPatientByUsername")
    public ResponseEntity<Optional<Patient>> getPatientById (@PathVariable("username") String username) throws NotFoundException {
        return patientService.getPatientByUsername(username);
    }

    @PutMapping("{id_patient}/modifyPatient")
    public ResponseEntity<MessageResponse> modifyPatient(@PathVariable("id_patient") Long id_patient, @RequestBody Patient patient, @RequestHeader(value = "Authorization") String token ) {
        return patientService.modifyPatient(id_patient,patient,token);
    }

    @DeleteMapping("/deletePatient")
    public ResponseEntity<?> deletePatient (@RequestHeader("Authorization") String token) {
        return patientService.deletePatient(token) ;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            String token = patientService.forgotPassword(email);
            String emailMessage = "An email with instructions to reset your password has been sent to your email address.";
            String tokenMessage = token;
            return ResponseEntity.ok().body(new ForgotPasswordResponse(emailMessage, tokenMessage));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.badRequest().body("User not found.");
        } catch (MessagingException | IOException | com.sun.xml.messaging.saaj.packaging.mime.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            patientService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Your password has been reset successfully.");
        } catch (InvalidTokenException ex) {
            return ResponseEntity.badRequest().body("Invalid token.");
        }
    }


}
