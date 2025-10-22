package br.edu.ifpb.veritas.models;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@DiscriminatorValue("STUDENT")
public class Student extends User {
   private String registrationNumber;
   private String course;

   @OneToMany(mappedBy = "author")
   private List<AcademicCase> academicCases;
   
}
