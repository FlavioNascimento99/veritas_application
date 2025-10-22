package br.edu.ifpb.veritas.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "academic_cases")
@Data
public class AcademicCase {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String subject;
   private String description;
   private LocalDate creationDate;
   // Você pode adicionar um Enum para o status: ABERTO, EM_JULGAMENTO, DEFERIDO, INDEFERIDO
   // private CaseStatus status;


   @ManyToOne
   @JoinColumn(name = "student_id")
   private Student author;
   
   // O coordenador é quem distribui, mas talvez não precise estar no processo em si.
   // A distribuição pode ser um evento ou uma informação no fluxo.

   @ManyToOne
   @JoinColumn(name = "rapporteur_id") // Relator
   private Professor rapporteur;

   @ManyToOne
   @JoinColumn(name = "board_meeting_id")
   private BoardMeeting boardMeeting; // Reunião em que foi pautado
}
