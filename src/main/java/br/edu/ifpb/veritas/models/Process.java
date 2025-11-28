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
import jakarta.persistence.OneToOne;
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
    * Log 1: Estudante que criara o processo em questão.
    * 
    * O Processo poderá apenas ser criado por um único
    * Estudante.
    */
   @OneToOne
   @JoinColumn(name = "PROCESS_STUDENT_ID", nullable = false)
   private Student student;

   /**
    * Log 1: Assim como o estudante emissor do processo, 
    * para o mesmo, será interessante que possua apenas
    * um "tema".
    * 
    * Não existe processo de "N" tipos/temática.
    */
   @OneToOne
   @JoinColumn(name = "PROCESS_SUBJECT_ID", nullable = false)
   private Subject subject;


   /**
    * Log 1: Seguinte, pra mim é "One-To-One", visto que,
    * existirá, o que aqui refere-se, relator do processo.
    * 
    * O relator do processo será também único, porém, nullable
    * já que no momento inicial, "não existirá".
    */
   @OneToOne
   @JoinColumn(name = "PROCESS_RAPPORTEUR_ID", nullable=true)
   private Professor professor;



   /** 
    * Relacionamento Many-To-One vem pelo fato de
    * que teremos um Collegiate com N Processes.
    */
   @ManyToOne
   @JoinColumn(name="COLLEGIATE_ID", nullable=true)
   private Collegiate collegiate;


   /**
    * 
    */
   @Enumerated(EnumType.STRING)
   private StatusProcess status;
   
   
   /**
    * Informações básicas estruturais da classe
    */
   private String title; 
   private String description;

   /**
    * 
    */
   private String validation;

   private LocalDateTime createdAt;
   private LocalDateTime distributedAt;
   private LocalDateTime solvedAt;
}