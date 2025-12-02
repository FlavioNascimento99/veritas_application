package br.edu.ifpb.veritas.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_professors")
@DiscriminatorValue("PROFESSOR")

// Evita recursão infinita ao serializar objetos com referências bidirecionais
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Professor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Required fields for Postman test
   */
  private String name;
  private String phoneNumber;
  private String register;
  private String login;
  private String password;
  private boolean isCoordinator = false;
  private boolean isActive      = true;

  /**
   * Listagem de Processos anexados ao professor.
   *
   * Nov Substituição de tipagem de ArrayList para List
   * Spring tenta utilizar PersistentBag mas não consegue
   * lidar com listas dinâmicas tão bem quanto listas de
   * tamanho pre-definido.
   */
//  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<Process> forwardedProcesses;

  @ManyToMany(mappedBy = "members")
  private List<Collegiate> collegiates = new ArrayList<>(); // participa de 1..* colegiados

  // Não deveria existir algo assim associando os processos ao professor relator?
  @OneToMany(mappedBy = "professor")
  private List<Process> forwardedProcesses; // relator de processos

  @OneToMany(mappedBy = "professor")
  private List<Vote> votes = new ArrayList<>(); // 0.. * votos

}
