package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ifpb.veritas.enums.StatusProcess;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_processes")
public class Process {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * 1. Estudante criador do Processo em questão
    * 2. "Categoria" do Processo
    */
   @ManyToOne
   @JoinColumn(name = "student_id", nullable = false)
   private Student student;

   @ManyToOne
   @JoinColumn(name = "subject_id", nullable = false)
   private Subject subject;


   /**
    * Professor dentro desse contexto trata-se
    * de Relator, se sentirem necessidade de 
    * alterar o identificador, tudo bem.
    */
   @ManyToOne
   @JoinColumn(name = "professor_id")
   private Professor professor;



   /** 
    * Criação de uma tabela intermediária    
    * para listagem dos professores presentes
    * no processo dentro de uma tabela.
    */
   @OneToMany
   @JoinTable(
      name = "process_validation_committee",
      joinColumns =  @JoinColumn(name = "process_id"),
      inverseJoinColumns = @JoinColumn(name = "professor_id")
   )
   private List<Professor> validationCommittee;

   @Enumerated(EnumType.STRING)
   private StatusProcess status;
   private String title;
   
   @Column(columnDefinition = "TEXT")
   private String description;
   private String validation;

   private LocalDateTime createdAt;
   private LocalDateTime distributedAt;
   private LocalDateTime solvedAt;
}
