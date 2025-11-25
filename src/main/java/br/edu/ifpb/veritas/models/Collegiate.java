package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_collegiate")
public class Collegiate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Rapporteur é o Professor relator do Colegiado.
   */
  @ManyToOne
  @JoinColumn(name = "professor_id")
  private Professor rapporteur;

  /**
   * Lista de processos relacionados ao Colegiado.
   * Unidirecional OneToMany com tabela de junção.
   */
  @OneToMany
  @JoinTable(
          name = "collegiate_processes",
          joinColumns = @JoinColumn(name = "collegiate_id"),
          inverseJoinColumns = @JoinColumn(name = "process_id")
  )
  private List<Process> processes = new ArrayList<>();

  private LocalDateTime createdAt;
}