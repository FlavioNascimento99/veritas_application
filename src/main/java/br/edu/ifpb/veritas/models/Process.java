package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;

import br.edu.ifpb.veritas.enums.StatusProcess;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
   
   
   // Relationship props
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne
   @JoinColumn(name = "student_id", nullable = false)
   private Student student;

   @ManyToOne
   @JoinColumn(name = "subject_id", nullable = false)
   private Subject subject;

   @ManyToOne
   @JoinColumn(name = "professor_id")
   private Professor professor;


   // Process properties
   @Enumerated(EnumType.STRING)
   private StatusProcess status;

   private String title;
   
   @Column(columnDefinition = "TEXT")
   private String description;


   // Professor's return about Process came from Coordinator
   private String technicalOpinion;


   // Timestamp properties
   private LocalDateTime createdAt;
   private LocalDateTime distributedAt;
   private LocalDateTime solvedAt;
}
