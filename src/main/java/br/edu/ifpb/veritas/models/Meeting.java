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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

  /**
   * Deveríamos ter um título? pensando sobre
   * poderíamos nomear, partindo do presuposto
   * de que, podemos ou não buscar por determinadas
   * reuniões, que não soa muito lógico pra mim
   */

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

  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
