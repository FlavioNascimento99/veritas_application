package br.edu.ifpb.veritas.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.repositories.UserRepository;

@Component
public class CoordinatorInitializer implements CommandLineRunner {
  @Autowired
  private UserRepository userRepository;

  @Override
  public void run(String... args) throws Exception {
    coordinatorObjectExampleChecker();
  }

  private void coordinatorObjectExampleChecker() {
    String coordLoginExample = "coord@veritas";
    userRepository.findByLogin(coordLoginExample)
      .ifPresentOrElse(
          coord -> System.out.println("Usuário coordenador já existe"),
          ()    -> coordinatorObjectExampleCreator(coordLoginExample));  
  }

  private void coordinatorObjectExampleCreator(String coordLogin) {
    User coord = new User();
    coord.setName("Coordenador Exemplo");
    coord.setLogin(coordLogin);
    coord.setPassword("senhasegura123");
    coord.setRole(UserRole.COORDENADOR);

    System.out.println("Coordenador devidamente criado");
    userRepository.save(coord);
  }
}
