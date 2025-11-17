/**
 *    @Author: Flavio Nascimento
 *    
 *    Classe de construção de Subject/Assunto de referência durante inicialização
 *    da aplicação.
 * 
 *    A dendo, estou utilizando de título para consulta de Banco, mas acredito que 
 *    existam outros dados que podem soar mais lúcidos para tal.
 */

package br.edu.ifpb.veritas.configurations;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.repositories.SubjectRepository;

@Component
public class SubjectInitializer implements CommandLineRunner {

   @Autowired
   private SubjectRepository subjectRepository;

   @Override
   public void run(String... args) throws Exception {
      createSubject();
   }

   private void createSubject() {
      String subjectExample = "MATRICULA";
      subjectRepository.findByTitle(subjectExample)
         .ifPresentOrElse(
            subject -> System.out.println("Assunto de Teste - Já existente em Banco de Dados"),
            () -> createExampleSubject(subjectExample)   
         );
   }

   private void createExampleSubject(String subjectTitle) {
      Subject subject = new Subject();
      subject.setTitle("MATRICULA");
      subject.setDescription("Exemplo de assunto referencia para processo interno da aplicação afim de efetuar testes.");
      subject.setActive(true);
      subject.setCreatedAt(LocalDateTime.now());
      
      subjectRepository.save(subject);
      System.out.println("Assunto de Referência [" + subject.getTitle() + "] - Criado com sucesso");
   }

}
