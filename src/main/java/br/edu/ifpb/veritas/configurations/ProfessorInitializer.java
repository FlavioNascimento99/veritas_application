package br.edu.ifpb.veritas.configurations;
import br.edu.ifpb.veritas.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;

@Component
public class ProfessorInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
   @Autowired
   private ProfessorRepository professorRepository;

    ProfessorInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   @Override
   public void run(String... args) throws Exception {
      professorObjectExampleChecker();
   }

   private void professorObjectExampleChecker() {
      String professorNameExample = "Frederico Guedes";
      professorRepository.findByName(professorNameExample)
         .ifPresentOrElse(
            professor -> System.out.println("Professor já se encontra registrado em Banco."),
            ()        -> professorObjectExampleCreator(professorNameExample));
   }

   private void professorObjectExampleCreator(String professorName) {
      Professor professor = new Professor();
      professor.setName(professorName);
      professor.setLogin("frederico@veritas.ifpb.br");
      professor.setPassword("senhasegura123");
      professor.setRole(UserRole.PROFESSOR);
      professor.setForwardedProcesses(null);

      userRepository.save(professor);
   }
}
