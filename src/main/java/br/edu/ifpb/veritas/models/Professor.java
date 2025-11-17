package br.edu.ifpb.veritas.models;

import java.util.ArrayList;

import org.hibernate.annotations.DiscriminatorOptions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("PROFESSOR")
public class Professor extends User {

  private ArrayList<Process> forwardedProcesses;

}
