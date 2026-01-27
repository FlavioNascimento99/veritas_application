package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ifpb.veritas.enums.VoteType;
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

   @Column(name = "TITLE")
   private String title;

   @Column(name = "DESCRIPTION")
   private String description;

   @Column(name = "NUMBER")
   private String number;

   @Column(name = "CREATED_AT")
   private LocalDateTime createdAt;

   @Column(name = "DISTRIBUTED_AT")
   private LocalDateTime distributedAt;

   @Column(name = "SOLVED_AT")
   private LocalDateTime solvedAt;

   private String opinion;

   @Column(name = "PROCESS_STATUS")
   @Enumerated(EnumType.STRING)
   private StatusProcess status;

   @Column(name = "RAPPORTEUR_VOTE")
   @Enumerated(EnumType.STRING)
   private VoteType rapporteurVote;

   @Column(name = "PROCESS_ORDER")
   private String processOrder;

   @ManyToOne
   @JoinColumn(name = "INTERESTED_STUDENT_ID", nullable = false)
   private Student processCreator;

   @ManyToOne
   @JoinColumn(name = "PROCESS_SUBJECT_ID", nullable = false)
   private Subject subject;

   @ManyToOne
   @JoinColumn(name = "PROFESSOR_RAPPORTEUR_ID", nullable = true)
   private Professor processRapporteur;

   @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
   private List<Vote> processVoteList;
}