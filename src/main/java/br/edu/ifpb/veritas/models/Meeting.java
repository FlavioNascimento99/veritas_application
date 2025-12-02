/**
 * Classe de Reunião
 *
 *
 * Responsável por gerar uma Ata (Documentação referente à si mesmo)
 * que possuirá Colegiado e uma Lista de Processos.
 *
 * questionamentos? deverá possuir mais alguma prop?
 */

package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ifpb.veritas.enums.MeetingStatus;
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
@Table(name = "tb_meetings")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Meeting {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Deveríamos ter um título? pensando sobre
   * poderíamos nomear, partindo do presuposto
   * de que, podemos ou não buscar por determinadas
   * reuniões, que não soa muito lógico pra mim
   */

  // Pertence a um Colegiado
  @ManyToOne
  @JoinColumn(name = "collegiate_id")
  private Collegiate collegiate; // 1 colegiado ao qual a reunião pertence

  // Lista de Processos em pauta na reunião
  @ManyToMany
  @JoinTable(
          name = "meeting_processes",
          joinColumns = @JoinColumn(name = "meeting_id"),
          inverseJoinColumns = @JoinColumn(name = "process_id")
  )
  private List<Process> processes; // 0..* processos em pauta

  private LocalDateTime createdAt;
  private MeetingStatus status;

  // Acredito que a ata foi adiada foi adiada
  // para a próxima etapa do projeto

}