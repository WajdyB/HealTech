package com.authentification.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(	name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doctor")
    private Long id_doctor ;

    @Column(name = "firstname")
    private String firstname ;

    @Column(name = "lastname")
    private String lastname ;

    @Column(name = "speciality")
    private String speciality ;

    @Column(name = "email")
    private String email ;

    @Column(name="password")
    @NotBlank
    @Size(max = 120)
    private String password;
}
