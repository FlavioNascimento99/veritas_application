package br.edu.ifpb.veritas.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
  private Boolean isActive = true;
  /**
   *    Necessário trocar de arraylist por list, vista o problema com valor       fixo entregue por arraylist
   *  Listagem de processos do mesmo poderá ser salvo dentro deste atributo.
   *  (Incerto da necessidade)
   */

//  @OneToMany
//  @JoinColumn(name="CREATED_PROCESSES")
//  private List<Process> createdProcesses;

  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
  private List<Process> interestedStudent; // interessado em processos

  @OneToOne(mappedBy = "representativeStudent")
  private Collegiate representativeCollegiate;
}