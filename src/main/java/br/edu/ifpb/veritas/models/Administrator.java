package br.edu.ifpb.veritas.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends User {

}
