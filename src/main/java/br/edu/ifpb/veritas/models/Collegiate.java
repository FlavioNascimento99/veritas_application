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
@Table(name = "TB_COLLEGIATE")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Collegiate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "CREATED_AT")
  private LocalDateTime createdAt;

  @Column(name = "CLOSED_AT")
  private LocalDateTime closedAt;

  @Column(name = "DESCRIPTION")
  private String description;


  /**
   * Rapporteur é o Professor/Relator do Processo.
   * Criada uma tabela com o ID do Professor em questão.
    @ManyToOne
    @JoinColumn(name = "PROFESSOR_ID")
    private Professor rapporteur;
  */
  
  
  /**
   * Log 1: Estou escervendo essa propriedade com a ideia de, Colegiado, nada mais é 
   * que, o grupo 
   */
  @ManyToMany
  @JoinTable(
    name                  = "MEMBERS", 
      joinColumns         = @JoinColumn(name = "COLLEGIATE_ID"), 
      inverseJoinColumns  = @JoinColumn(name = "PROFESSOR_ID")
  )
  private List<Professor> collegiateMemberList;

  /**
   * Log 1: Listagem de Processos de um determinado Colegiado realmente será necessário?
   * 
  */
  @OneToMany(mappedBy = "collegiate", cascade = CascadeType.ALL)
  private List<Meeting> collegiateMeetingsList;
  
  
  /**
   * Relacionamento com aluno representante
   * 
   */
  @OneToOne
  @JoinColumn(name = "COLLEGIATE_REP_STUDENT_ID", nullable = true)
  private Student representativeStudent;

  /**
   * Log 1: Listagem de processos sob análise pelo colegiado.
   *  mappedBy refere ao atributo de mesmo nome em Process
   *  Colegiado não deveria ter uma lista de processos,
   *  é a reunião que os julga
   * 
   * 
    @OneToMany(mappedBy="collegiate")
    private List<Process> processesToJudgeList;
   *
   */
  
}