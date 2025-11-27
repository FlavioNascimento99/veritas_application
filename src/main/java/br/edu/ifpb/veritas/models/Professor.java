package br.edu.ifpb.veritas.models;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
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
  private boolean isActive = true;

  /**
   * Listagem de Processos anexados ao professor. 
   * 
   * Nov Substituição de tipagem de ArrayList para List
   * Spring tenta utilizar PersistentBag mas não consegue
   * lidar com listas dinâmicas tão bem quanto listas de 
   * tamanho pre-definido.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Process> forwardedProcesses;
}
