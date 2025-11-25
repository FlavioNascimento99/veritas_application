package br.edu.ifpb.veritas.configurations;

import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.repositories.AdminRepository;

@Component
public class CoordinatorInitializer implements CommandLineRunner {

  @Autowired
  private ProfessorRepository professorRepository;

  @Override
  public void run(String... args) throws Exception {
    coordinatorObjectExampleChecker();
  }

  private void coordinatorObjectExampleChecker() {
    String coordLoginExample = "coord@veritas";
    professorRepository.findByLogin(coordLoginExample)
      .ifPresentOrElse(
          coord -> System.out.println("Usuário coordenador já existe"),
          ()    -> coordinatorObjectExampleCreator(coordLoginExample));  
  }

  private void coordinatorObjectExampleCreator(String coordLogin) {
    Professor coord = new Professor();
    coord.setName("Coordenador Exemplo");
    coord.setLogin(coordLogin);
    coord.setPassword("senhasegura123");
    // coord.setRole(UserRole.COORDENADOR);

    System.out.println("Coordenador devidamente criado");
    professorRepository.save(coord);
  }
}
