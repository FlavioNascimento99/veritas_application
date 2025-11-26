package br.edu.ifpb.veritas.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;

@Component
public class ProfessorInitializer implements CommandLineRunner {

   @Autowired
   private ProfessorRepository professorRepository;

//    ProfessorInitializer(AdminRepository userRepository) {
//        this.userRepository = userRepository;
//    }

   ProfessorInitializer(ProfessorRepository professorRepository) {
      this.professorRepository = professorRepository;
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
      professor.setForwardedProcesses(null);

      professorRepository.save(professor);
   }
}
