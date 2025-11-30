package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ifpb.veritas.enums.StatusProcess;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Process {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * Informações básicas estruturais da classe
    */
   private String title;
   private String description;
   private String number;
   private LocalDateTime createdAt;
   private LocalDateTime distributedAt;
   private LocalDateTime solvedAt;
   private String opinion;

   @Enumerated(EnumType.STRING)
   private StatusProcess status;

   /**
    * Log 1: Estudante que criara o processo em questão.
    *
    * O Processo poderá apenas ser criado por um único
    * Estudante.
    */
   @ManyToOne
   @JoinColumn(name = "interestedStudent_id", nullable = false)
   private Student student;

   /**
    * Log 1: Assim como o estudante emissor do processo,
    * para o mesmo, será interessante que possua apenas
    * um "tema".
    *
    * Não existe processo de "N" tipos/temática.
    */
   @ManyToOne
   @JoinColumn(name = "PROCESS_SUBJECT_ID", nullable = false)
   private Subject subject;

   /**
    * Log 1: Seguinte, pra mim é "One-To-One", visto que,
    * existirá, o que aqui refere-se, relator do processo.
    *
    * O relator do processo será também único, porém, nullable
    * já que no momento inicial, "não existirá".
    */
   @ManyToOne
   @JoinColumn(name = "professor_relator_id", nullable=true)
   private Professor professor; // professor relator

   @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
   private List<Vote> votes; // Votos dos membros do colegiado neste process

   /**
    * Relacionamento Many-To-One vem pelo fato de
    * que teremos um Collegiate com N Processes.
    */
// Não é o collegiate que irá gerir o processo
// @ManyToOne
// @JoinColumn(name="COLLEGIATE_ID", nullable=true)
// private Collegiate collegiate;

}