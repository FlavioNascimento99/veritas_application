package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_COLLEGIATE")
public class Collegiate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  /**
   * Lista de professores que compõem o colegiado
   */
  @ManyToMany
  private List<Professor> professores;

  /**
   * Listagem de processos sob análise pelo colegiado.
   * mappedBy refere ao atributo de mesmo nome em Process
   */
  @OneToMany(mappedBy="collegiate")
  private List<Process> processes;

  private LocalDateTime createdAt;
}