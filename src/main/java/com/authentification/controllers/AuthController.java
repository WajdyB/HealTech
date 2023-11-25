package com.authentification.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.authentification.entities.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentification.entities.ERole;
import com.authentification.entities.Role;
import com.authentification.payload.JwtResponse;
import com.authentification.payload.LoginRequest;
import com.authentification.payload.MessageResponse;
import com.authentification.payload.SignupRequest;
import com.authentification.repository.RoleRepository;
import com.authentification.repository.PatientRepository;

import com.authentification.jwt.JwtUtils;
import com.authentification.userdetails.UserDetailsImpl;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PatientRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup/{role}")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest,@PathVariable ("role") String roleOfUser) {
		if (userRepository.existsByUsername(signUpRequest.getFirstname())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new patient's account
		Patient patient = new Patient();
		patient.setFirstname(signUpRequest.getFirstname());
		patient.setLastname(signUpRequest.getLastname());
		patient.setUsername(signUpRequest.getUsername());
		patient.setEmail(signUpRequest.getEmail());
		patient.setPhone(signUpRequest.getPhone());
		patient.setGender(signUpRequest.getGender());
		patient.setPassword(encoder.encode(signUpRequest.getPassword()));
		patient.setAddress(signUpRequest.getAddress());

		Set<Role> roles = new HashSet<>();
		
		if(roleOfUser!=null) {
			if(roleOfUser.equals("admin")) {
				Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				roles.add(adminRole);
				}
			else if (roleOfUser.equals("patient")) {
				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				roles.add(userRole);
			 }
			} 

		patient.setRoles(roles);
		userRepository.save(patient);

		return ResponseEntity.ok(new MessageResponse("Patient registered successfully!"));
	}
}
