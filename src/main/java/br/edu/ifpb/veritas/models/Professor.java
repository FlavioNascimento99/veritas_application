package br.edu.ifpb.veritas.models;

import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_professors")
@DiscriminatorValue("PROFESSOR")
public class Professor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String phoneNumber;
  private String register;
  private String login;
  private String password;
  private boolean isCoordinator;

  // Lista de processos encaminhados pelo professor
  private ArrayList<Process> forwardedProcesses;
}
