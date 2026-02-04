package br.edu.ifpb.veritas.controllers.web;

import br.edu.ifpb.veritas.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Partindo do ponto em que temos um administrador já
 * cadastrado dentro da plataorma.
 * <p>
 * Aqui vamos trabalhar com a interação do Administrador
 * que por sua vez, poderá cadastrar estudantes e professores.
 * <p>
 * Este controller é protegido pelo Spring Security. Apenas usuários
 * com o perfil 'ADMIN' podem acessar seus métodos.
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    private final RegistrationService registrationService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("pageTitle", "Registro de Usuário");
        model.addAttribute("activePage", "register");
        return "pages/register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("userType") String userType,
            RedirectAttributes redirectAttributes
    ) {
        registrationService.registerUser(fullName, email, password, confirmPassword, userType);
        redirectAttributes.addFlashAttribute("successMessage", "Conta criada com sucesso! Faça o login.");
        return "redirect:/login";
    }
}
