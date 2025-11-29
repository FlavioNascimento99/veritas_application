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
import java.util.ArrayList;
import java.util.List;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "tb_meetings")
public class Meeting {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  
  // private String title = LocalDateTime.now();

  @ManyToOne
  @JoinColumn(name = "collegiate_id")
  private Collegiate collegiate;

  /**
   * Listagem de processos sob análise pela Reunião.
   * mappedBy refere ao atributo de mesmo nome em Process
   */
  @OneToMany(mappedBy="collegiate")
  private List<Process> processes;

  // Professores participantes (subset dos membros do colegiado)
  @ManyToMany
  @JoinTable(
        name = "reuniao_professores",
        joinColumns = @JoinColumn(name = "reuniao_id"),
        inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
  private List<Professor> membrosPresentes = new ArrayList<>();

  // Processos colocados na pauta da reunião
  @ManyToMany
  @JoinTable(
        name = "reuniao_pauta",
        joinColumns = @JoinColumn(name = "reuniao_id"),
        inverseJoinColumns = @JoinColumn(name = "processo_id")
    )
  private List<Process> pauta = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  private MeetingStatus status;


  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
