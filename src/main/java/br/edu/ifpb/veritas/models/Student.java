package br.edu.ifpb.veritas.models;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "TB_STUDENT")
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
  /** 
   *    Necessário trocar de arraylist por list, vista o problema com valor       fixo entregue por arraylist 
   *  Listagem de processos do mesmo poderá ser salvo dentro deste atributo.
   *  (Incerto da necessidade)
  */
  @OneToMany
  @JoinColumn(name="CREATED_PROCESSES")
  private List<Process> createdProcesses;
  private Boolean isActive = true;

}
