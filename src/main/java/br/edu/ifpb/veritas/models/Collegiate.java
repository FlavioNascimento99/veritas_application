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

  

  private LocalDateTime createdAt;
}