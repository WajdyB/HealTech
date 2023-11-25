package com.authentification.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(	name = "patients")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_report")
    private Long id_report ;

    @Column(name = "heart_rate")
    private String heart_rate ;

    @Column(name = "blood_pressure")
    private String blood_pressure ;

    @Column(name = "glucose_level")
    private String glucose_level ;

    @Column(name = "activity_levels")
    private String activity_levels ;

    @Column(name = "medication_adherence")
    private String medication_adherence ;

}
