package br.edu.ifpb.veritas.models;

import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_students")
@DiscriminatorValue("STUDENT")
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String phoneNumber;
  // Matrícula
  private String register;
  private String login;
  private String password;

  // Listagem de processos do mesmo poderá ser salvo dentro deste atributo.
  // (Incerto da necessidade)
  private ArrayList<Process> createdProcesses;
  private Boolean isActive = true;

}
