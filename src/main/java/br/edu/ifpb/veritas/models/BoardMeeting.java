package br.edu.ifpb.veritas.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class BoardMeeting {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private LocalDate meetingDate;
   // Você pode adicionar um Enum para o status: AGENDADA, EM_ANDAMENTO, FINALIZADA
   // private MeetingStatus status;

   @OneToMany(mappedBy = "boardMeeting")
   private List<AcademicCase> agenda; // Pauta da reunião

   @ManyToMany
   @JoinTable(name = "board_meeting_members")
   private List<Professor> members; // Membros do colegiado que participam
}
