package br.edu.ifpb.veritas.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.models.Process;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long>, JpaSpecificationExecutor<Process> {
  List<Process> findByProcessCreator_Id(Long studentId);

  List<Process> findByProcessRapporteur_Id(Long professorId);

  List<Process> findBySubjectId(Long subjectId);

  List<Process> findByStatus(StatusProcess status);

  Optional<Process> findByTitle(String title);

  // Queries específicas
  @Query("SELECT p FROM Process p WHERE p.processRapporteur.id = :professorId AND p.status = 'UNDER_ANALISYS'")
  List<Process> findProfessorIdleProcesses(@Param("professorId") Long professorId);

  @Query("SELECT p FROM Process p WHERE p.createdAt BETWEEN :start AND :end")
  List<Process> findByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

  @Query("SELECT p FROM Process p WHERE p.status IN (br.edu.ifpb.veritas.enums.StatusProcess.WAITING, br.edu.ifpb.veritas.enums.StatusProcess.UNDER_ANALISYS)")
  List<Process> findPendingProcesses();

  @Query("SELECT COUNT(p) FROM Process p WHERE p.processRapporteur.id = :professorId AND p.status IN (br.edu.ifpb.veritas.enums.StatusProcess.APPROVED, br.edu.ifpb.veritas.enums.StatusProcess.REJECTED)")
  Long countProcessesFinishedByProfessor(@Param("professorId") Long professorId);

  @Query("SELECT p FROM Process p WHERE p.processCreator.id = :studentId " +
      "ORDER BY p.createdAt DESC")
  List<Process> findLastProcessesFromStudent(@Param("studentId") Long studentId);

  // Contadores
  Long countByStatus(StatusProcess status);
  Long countByProcessCreator_IdAndStatus(Long studentId, StatusProcess status);
  List<Process> findByStatusIn(List<StatusProcess> statusProcess);

  // ========== REQNAOFUNC 9: Métodos com Paginação ==========
  // Busca processos por status com paginação
  Page<Process> findByStatus(StatusProcess status, Pageable pageable);

  // Busca processos por estudante criador com paginação
  Page<Process> findByProcessCreator_Id(Long studentId, Pageable pageable);

  // Busca processos por professor relator com paginação
  Page<Process> findByProcessRapporteur_Id(Long professorId, Pageable pageable);
}
