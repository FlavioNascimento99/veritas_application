package br.edu.ifpb.veritas.configurations;

import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StudentInitializer implements CommandLineRunner {
   
//   @Autowired
//   private AdminRepository userRepository;

   @Autowired
   private StudentRepository studentRepository;
   
   @Override public void run(String ...args) throws Exception {
      studentObjectExampleChecker();
   }

   private void studentObjectExampleChecker() {
      String studentLoginExample = "student@veritas";
      studentRepository.findByLogin(studentLoginExample)
         .ifPresentOrElse(
            user  -> System.out.println("Estudante já fora criado"),
            ()    -> studentObjectExampleCreator(studentLoginExample)
         );
   }

   private void studentObjectExampleCreator(String loginString) {
      Student student = new Student();
      student.setName("Estudante Exemplo");
      student.setLogin(loginString);
      student.setPassword("senhasegura123");
      // student.setRole(UserRole.ESTUDANTE);

      studentRepository.save(student);
      System.out.println("Estudante criado com sucesso");
   }
}
