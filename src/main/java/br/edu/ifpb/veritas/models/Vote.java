package br.edu.ifpb.veritas.models;

import br.edu.ifpb.veritas.enums.VoteType;
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
@Table(name = "tb_votes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Vote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private VoteType voteType;
  private Boolean away;

  @ManyToOne
  @JoinColumn(name = "process_id", nullable = false)
  private Process process;

  @ManyToOne
  @JoinColumn(name = "professor_id", nullable = false)
  private Professor professor;
}
