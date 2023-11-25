package com.authentification.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(	name = "patients",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "username"),
				@UniqueConstraint(columnNames = "email")
		})
public class Patient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_patient")
	private Long id_patient ;

	@Column (name="username")
	private String username;
	@Column (name="firstname")
	private String firstname;

	@Column (name="lastname")
	private String lastname ;

	@Column (name="email")
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@Column (name="phone")
	private int phone ;

	@Column (name="gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(name="password")
	@NotBlank
	@Size(max = 120)
	private String password;

	@Column (name="address")
	private String address ;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "patients_roles",
			joinColumns = @JoinColumn(name = "id_patient"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();



}