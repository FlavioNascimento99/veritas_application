/**
 * 
 *    @Author: Flavio Nascimento
 * 
 *    Classe de inicialização de Process example
 * 
 *    Utilizado para construção de um objeto do tipo Processo
 *    necessária a existência de outras 
 * 
 */

package br.edu.ifpb.veritas.configurations;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.models.Subject;
import br.edu.ifpb.veritas.repositories.ProcessRepository;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;
import br.edu.ifpb.veritas.repositories.StudentRepository;
import br.edu.ifpb.veritas.repositories.SubjectRepository;
import br.edu.ifpb.veritas.repositories.UserRepository;

@Component
public class ProcessInitializer implements CommandLineRunner {

  @Autowired
  private ProcessRepository processRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProfessorRepository professorRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private SubjectRepository subjectRepository;

  @Override
  public void run(String... args) throws Exception {
    processObjectExampleChecker();
  }


  /** 
   * Checa a existência da entidade exemplo dentro do Banco
   */
  private void processObjectExampleChecker() {
    /**
     * Título do Processo
     */
    String processTitleChecker = "Solicito Trancamento de Disciplina";
    processRepository.findByTitle(processTitleChecker)
        .ifPresentOrElse(
            process -> System.out.println("Processo de Teste - Já existe em Banco"),
            () -> processObjectExampleCreator(processTitleChecker));
  }


  /**
   * Em caso de inexistência em banco, por parte da Entidade de teste, criamos uma
   */
  private void processObjectExampleCreator(String title) {
    /**
     * Nesse caso temos um novo Processo sendo criado
     * que por sua vez necessita de outras Entidades 
     * para que possa ser possível. 
     * 
     * Pra resolver esse problema e inicializar o 
     * banco com N entidades de teste, implementei
     * .orElseGet para fazer a busca dentro do Banco
     * e reaproveitarmos o que pode já estar presente.
     */
    Process process = new Process();

    Student student = studentRepository.findByRegister("student@veritas")
        .orElseGet(() -> {
          Student newStudent = new Student();
          newStudent.setName("Estudante Exemplo");
          newStudent.setRegister("000000");
          newStudent.setLogin("student@veritas");
          newStudent.setPassword("senhasegura123");
          newStudent.setRole(UserRole.ESTUDANTE);
          
          return studentRepository.save(newStudent);
        });

    Professor professor = professorRepository.findByRegister("999999")
        .orElseGet(() -> {
          Professor newProfessor = new Professor();
          newProfessor.setName("Professor Exemplo");
          newProfessor.setRegister("000000");
          newProfessor.setLogin(title);
          newProfessor.setRole(UserRole.PROFESSOR);

          return professorRepository.save(newProfessor);
        });

    
    Subject subject = subjectRepository.findByTitle("MATRICULA")
        .orElseGet(() -> {
          Subject newSubject = new Subject();
          newSubject.setTitle("MATRICULA");
          newSubject.setDescription("Processos referentes à matrículas do alunado.");
          newSubject.setActive(true);
          newSubject.setCreatedAt(LocalDateTime.now());

          return subjectRepository.save(newSubject);
        });
    /**
     * Conclusão da criação de Processo.
     */
    process.setTitle(title);
    process.setDescription("Prezado coordenador, solicito o trancamento da disciplina X por motivos de...");
    process.setCreatedAt(LocalDateTime.now());
    process.setStatus(StatusProcess.UNDER_ANALISYS);
    process.setStudent(student);
    process.setProfessor(professor);
    process.setSubject(subject);
    
    processRepository.save(process);
    System.out.println("Processo de Teste criado com sucesso!");
  }
}
