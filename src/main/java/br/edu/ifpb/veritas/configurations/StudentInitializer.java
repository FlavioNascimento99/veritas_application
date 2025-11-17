package br.edu.ifpb.veritas.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.repositories.UserRepository;

@Component
public class StudentInitializer implements CommandLineRunner {
   
   @Autowired
   private UserRepository userRepository;
   
   @Override public void run(String ...args) throws Exception {
      studentObjectExampleChecker();
   }

   private void studentObjectExampleChecker() {
      String studentLoginExample = "student@veritas";
      userRepository.findByLogin(studentLoginExample)
         .ifPresentOrElse(
            user  -> System.out.println("Estudante já fora criado"),
            ()    -> studentObjectExampleCreator(studentLoginExample)
         );
   }

   private void studentObjectExampleCreator(String loginString) {
      User student = new User();
      student.setName("Estudante Exemplo");
      student.setLogin(loginString);
      student.setPassword("senhasegura123");
      student.setRole(UserRole.ESTUDANTE);

      userRepository.save(student);
      System.out.println("Estudante criado com sucesso");
   }
}
