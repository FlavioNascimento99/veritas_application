package br.edu.ifpb.veritas.models;

import br.edu.ifpb.veritas.enums.VoteType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_VOTES")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Vote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "vote_type")
  private VoteType voteType;
  
  /**
   * Log 1: Define se o mesmo (Professor membro do Colegiado) 
   * encontrou-se ausente da reunião para definir destino do 
   * Processo.
   */
  private Boolean away = false;


  // O columnDefinition é para permitir textos longos (que imagino que seria o caso, visto que é uma justificativa de um voto)
  @Column(name = "justification", columnDefinition = "TEXT")
  private String justification;

  // Registra a data e hora em que o voto foi realizado
  @Column(name = "voted_at")
  private LocalDateTime votedAt;

  /**
   * Log 1: Haverá N votos para um Processo, logo referencia 
   * a este segundo.
   */
  @ManyToOne
  @JoinColumn(name = "PROCESS_ID", nullable = true)
  private Process process;


  /**
   * Log 1: Professor referente ao voto em questão,
   * neste caso, pra mim, faz mais sentido o OneToOne.
   * 
   * Log 2: de ManyToOne para OneToOne.
   */
  @OneToOne
  @JoinColumn(name = "PROFESSOR_ID", nullable = false)
  private Professor professor;

}
