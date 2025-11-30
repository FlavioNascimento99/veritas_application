package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_collegiate")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Collegiate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime createdAt;
  private LocalDateTime endedAt;
  private String description;
  private String resolution; // Foi a tradução mais próxima que encontrei pra "portaria"
  private String course;

  /**
   * Rapporteur é o Professor/Relator do Processo.
   * Criada uma tabela com o ID do Professor em questão.
   */
//  @ManyToOne
//  @JoinColumn(name = "PROFESSOR_ID")
//  private Professor rapporteur;

  // Lista de membros do colegiado
  @ManyToMany
  @JoinTable(
          name = "tb_collegiate_professors",
          joinColumns = @JoinColumn(name = "collegiate_id"),
          inverseJoinColumns = @JoinColumn(name = "professor_id")
  )
  private List<Professor> members = new ArrayList<>(); // 1.. * professores (members)

  // Lista de reuniões do colegiado
  @OneToMany(mappedBy = "collegiate", cascade = CascadeType.ALL)
  private List<Meeting> meetings = new ArrayList<>(); // 0.. * reuniões (meetings)

  // Relacionamento com aluno representante
  @OneToOne
  @JoinColumn(name = "student_representative_id", nullable = true)
  private Student representativeStudent;

  /**
   * Listagem de processos sob análise pelo colegiado.
   * mappedBy refere ao atributo de mesmo nome em Process
   */
  // Colegiado não deveria ter uma lista de processos,
  // é a reunião que os julga
  //  @OneToMany(mappedBy="collegiate")
  //  private List<Process> processes;

}