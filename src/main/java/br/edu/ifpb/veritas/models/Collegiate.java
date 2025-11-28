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
   * Rapporteur é o Professor/Relator do Processo.
   * Criada uma tabela com o ID do Professor em questão.
   */
  @ManyToOne
  @JoinColumn(name = "PROFESSOR_ID")
  private Professor rapporteur;

  /**
   * Listagem de processos sob análise pelo colegiado.
   * mappedBy refere ao atributo de mesmo nome em Process
   */
  @OneToMany(mappedBy="collegiate")
  private List<Process> processes;

  private LocalDateTime createdAt;
}