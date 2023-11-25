package com.authentification.userdetails;



import com.authentification.entities.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authentification.repository.PatientRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	PatientRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Patient patient = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Patient Not Found with username: " + username));

		return UserDetailsImpl.build(patient);
	}

}
