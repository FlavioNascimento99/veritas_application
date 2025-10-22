package br.edu.ifpb.veritas.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@DiscriminatorValue("COORDINATOR")
public class CourseCoordinator extends Professor {
   // Até o momento não fora vista necessidades de criar métodos para cá
}

