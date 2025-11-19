package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "collegiate_tb")
public class Collegiate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  /**
   * Dentro desse contexto temos "rapporteur" como
   * o Professor relator do Colegiado.
   */
  private Professor rapporteur;

  @ManyToOne
  private List<Process> processes;

  private LocalDateTime created_at;
}
