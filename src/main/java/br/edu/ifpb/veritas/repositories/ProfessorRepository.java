package br.edu.ifpb.veritas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.veritas.models.Professor;


public interface ProfessorRepository extends JpaRepository <Professor, Long> {
   Optional<Professor> findByName(String name);
   Optional<Professor> findByRegister(String register);
}
