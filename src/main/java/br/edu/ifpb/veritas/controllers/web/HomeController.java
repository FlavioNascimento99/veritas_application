package br.edu.ifpb.veritas.controllers.web;

import java.time.Year;

import lombok.RequiredArgsConstructor;
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

      // Dados para componentes
      model.addAttribute("heroTitle", "Chega de papelada administrativa...");
      model.addAttribute("heroSubTitle", "Conheça a solução definitiva para controle total de projetos e requisições acadêmicas.");
      model.addAttribute("heroBtntext", "Entrar");
      model.addAttribute("heroBtnUrl", "/login");

      return "pages/index";
   }

   @GetMapping("/login")
   public String login(Model model) {
      model.addAttribute("pageTitle", "Login");
      model.addAttribute("activePage", "login");
      return "pages/login";
   }

   @GetMapping("/contact")
   public String contact(Model model) {
      model.addAttribute("pageTitle", "Contato");
      model.addAttribute("activePage", "contact");
      return "pages/contact";
   }

   @GetMapping("/about")
   public String about(Model model) {
      model.addAttribute("pageTitle", "Sobre");
      model.addAttribute("activePage", "about");
      return "pages/about";
   }

   // Adicionado para evitar erros 404 de favicon.ico no log
   @GetMapping("favicon.ico")
   @ResponseBody
   void returnNoFavicon() {
      // Método vazio para retornar uma resposta 200 OK sem corpo
   }
   
}
