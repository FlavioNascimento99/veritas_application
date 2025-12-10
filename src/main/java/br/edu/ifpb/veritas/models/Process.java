package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifpb.veritas.enums.DecisionType;
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
@Table(name = "TB_PROCESSES")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Process {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * Basic Data
    */
   @Column(name = "TITLE")
   private String title;

   @Column(name = "DESCRIPTION")
   private String description;

   @Column(name = "NUMBER")
   private String number;
 

   /**
    * Temporal Data
    */
   @Column(name = "CREATED_AT")
   private LocalDateTime createdAt;

   @Column(name = "DISTRIBUTED_AT")
   private LocalDateTime distributedAt;

   @Column(name = "SOLVED_AT")
   private LocalDateTime solvedAt;


   /**
    * ?? Não tenho mais certeza de nada quanto a isso daqui.
    */
   private String opinion;


   @Column(name = "PROCESS_STATUS")
   @Enumerated(EnumType.STRING)
   private StatusProcess status;

   @Column(name = "RAPPORTEUR_VOTE")
   private DecisionType rapporteurVote;


   /**
   * Log 1: Referente à Portaria do Processo.
   */
   @Column(name = "PROCESS_ORDER")
   private String processOrder;

   /**
    * Log 1: Estudante que criara o processo em questão.
    *
    * O Processo poderá apenas ser criado por um único
    * Estudante.
    */
   @ManyToOne
   @JoinColumn(name = "INTERESTED_STUDENT_ID", nullable = false)
   private Student processCreator;

   /**
    * Log 1: Assim como o estudante emissor do processo,
    * para o mesmo, será interessante que possua apenas
    * um "tema".
    *
    * Não existe processo de "N" tipos/temática.
    */
   @ManyToOne
   @JoinColumn(name = "PROCESS_SUBJECT_ID", nullable = false)
   private Subject subject ;

   /**
    * Log 1: Seguinte, pra mim é "One-To-One", visto que,
    * existirá, o que aqui refere-se, relator do processo.
    *
    * O relator do processo será também único, porém, nullable
    * já que no momento inicial, "não existirá".
    */
   @ManyToOne
   @JoinColumn(name = "PROFESSOR_RAPPORTEUR_ID", nullable= true)
   private Professor processRapporteur;


   /**
    * Log 1: Lista de Votos efetuados dentro deste processo.
    * 
    * Votos possui os ID de cada membro e seu respectivo
    * voto.
    */
   @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
   private List<Vote> processVoteList;
}