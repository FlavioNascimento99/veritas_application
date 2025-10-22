package br.edu.ifpb.veritas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ifpb.veritas.services.UserService;
import br.edu.ifpb.veritas.services.BoardMeetingService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BoardMeetingService boardMeetingService;

    @Autowired
    public AdminController(UserService userService, BoardMeetingService boardMeetingService) {
        this.userService = userService;
        this.boardMeetingService = boardMeetingService;
    }

    // REQFUNC 14: CRUD para usuários (alunos, professores, coordenadores)
    @GetMapping("/users")
    public String listUsers() {
        // Página para listar e gerenciar todos os usuários
        return "admin/users-list";
    }

    // REQFUNC 13: CRUD para colegiados
    @GetMapping("/boards")
    public String listBoards() {
        // Página para listar e gerenciar os colegiados (grupos de professores)
        return "admin/boards-list";
    }

    // REQFUNC 15: CRUD para assuntos de processos
    @GetMapping("/subjects")
    public String listSubjects() {
        // Página para listar e gerenciar os tipos de assunto dos processos
        return "admin/subjects-list";
    }

    // Outros métodos de CRUD (POST, PUT, DELETE) seriam adicionados aqui
}
