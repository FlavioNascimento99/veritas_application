/**
 *    @Author: Flavio Nasicmento
 * 
 *    Classe de construção do Super Usuário da aplicação
 * 
 *       Consiste em casos de inexistências, validada dentro do método createAdminUser()
 *    construir o mesmo dentro de createNewAdmin(). O método inicializador faz a chamada do 
 *    método de verificação.
 *       
 *       A maneira vista como necessária vista a tipagem escolhida para o método findByEmail()
 *    na classe UserRepository, que fora definida como Optional<User>.
 */

package br.edu.ifpb.veritas.configurations;

import br.edu.ifpb.veritas.models.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.repositories.AdminRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

  // @Autowired
  // private UserRepository userRepository;
  @Autowired
  private AdminRepository adminRepository;

  @Override
  public void run(String... args) throws Exception {
    adminObjectExampleChecker();
  }

  private void adminObjectExampleChecker() {
    String adminLoginExample = "admin@veritas";
    
    adminRepository.findByLogin(adminLoginExample)
        .ifPresentOrElse(
            user  -> System.out.println("Usuário Administrativo já existe!"),
            ()    -> adminObjectExampleChecker(adminLoginExample));
  }

  private void adminObjectExampleChecker(String adminLogin) {
    // User admin = new User();
    Administrator admin = new Administrator();
    
    admin.setName("Administrador Exemplo");
    admin.setLogin(adminLogin);
    admin.setRegister("010101");
    admin.setPassword("senhasegura123");
    // admin.setRole(UserRole.ADMIN);

    adminRepository.save(admin);
    System.out.println("Usuário Administrador fora criado [" + admin.getLogin() + "] - [" + admin.getPassword() + "]");
  }
}
