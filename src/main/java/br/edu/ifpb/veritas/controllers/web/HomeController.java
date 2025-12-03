package br.edu.ifpb.veritas.controllers.web;

import java.time.Year;

import br.edu.ifpb.veritas.services.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
/**
 * Controlador responsável por páginas: Login, Homepage da Aplicação e Login com sucesso.
 */

@Controller
@RequiredArgsConstructor
public class HomeController {

   private final SubjectService subjectService;

   @ModelAttribute
   public void addCommonAttribute(Model model, HttpSession http) {
      model.addAttribute("SystemName", "VERITAS");
      model.addAttribute("actualYear", Year.now().getValue());
      model.addAttribute("version", "0.0.3");

      // Atualmente não vamos precisar de mais atributos.
   }

   @GetMapping("/")
   public String home(Model model) {
      // Metadados para carregamento de dados
      model.addAttribute("pageTitle", "VERITAS - Aplicação de controle e gerenciamento institucional.");
      model.addAttribute("pageDescription", "A melhor aplicação para controle acadêmico ");
      model.addAttribute("activePage", "home");
      model.addAttribute("mainContent", "pages/index :: mainContent");;

      // Dados para componentes
      model.addAttribute("heroTitle", "Chega de papelada administrativa...");
      model.addAttribute("heroSubTitle", "Conheça a solução definitiva para controle total de projetos e requisições acadêmicas.");
      model.addAttribute("heroBtntext", "Entrar");
      model.addAttribute("heroBtnUrl", "/login");

      return "home";
   }

   @GetMapping("/login")
   public String login(Model model) {
      model.addAttribute("pageTitle", "Login");
      model.addAttribute("activePage", "login");
      model.addAttribute("mainContent", "pages/login :: content");
      return "home";
   }

   @GetMapping("/contact")
   public String contact(Model model) {
      model.addAttribute("pageTitle", "Contato");
      model.addAttribute("activePage", "contact"); 
      model.addAttribute("mainContent", "pages/contact :: content");
      return "home";
   }

   @GetMapping("/about")
   public String about(Model model) {
      model.addAttribute("pageTitle", "Sobre");
      model.addAttribute("activePage", "about");
      model.addAttribute("mainContent", "pages/about :: content");
      return "home";
   }

   // Adicionado para evitar erros 404 de favicon.ico no log
   @GetMapping("favicon.ico")
   @ResponseBody
   void returnNoFavicon() {
      // Método vazio para retornar uma resposta 200 OK sem corpo
   }
   
}
