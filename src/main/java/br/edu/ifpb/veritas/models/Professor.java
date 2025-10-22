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
@DiscriminatorValue("PROFESSOR")
public class Professor extends User {
   private String department;

   @OneToMany(mappedBy = "rapporteur")
   private List<AcademicCase> cases;
}
