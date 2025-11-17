package br.edu.ifpb.veritas.models;

import java.util.ArrayList;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("STUDENT")
public class Student extends User {
  // Listagem de processos do mesmo poderá ser salvo dentro deste atributo.
  // (Incerto da necessidade)
  private ArrayList<Process> createdProcesses;

}
