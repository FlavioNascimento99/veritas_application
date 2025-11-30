package br.edu.ifpb.veritas.models;

import br.edu.ifpb.veritas.enums.VoteType;
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
@Table(name = "tb_votes")
public class Vote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;
  private VoteType voteType;
  private Boolean away;

  @ManyToOne
  @JoinColumn(name = "process_id", nullable = false)
  private Process process;

  @ManyToOne
  @JoinColumn(name = "professor_id", nullable = false)
  private Professor professor;
}
