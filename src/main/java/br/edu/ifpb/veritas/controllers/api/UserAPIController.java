package br.edu.ifpb.veritas.controllers.api;

import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAPIController {

    private final UserService userService;

    // REQFUNC 14: CRUD para alunos, professores e coordenadores
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> listAllUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    // Endpoint útil para o Coordenador listar professores (REQFUNC 8)
    // e para o Admin listar usuários por papel.
    @GetMapping("/by-role")
    public ResponseEntity<List<User>> listUsersByRole(@RequestParam UserRole role) {
        return ResponseEntity.ok(userService.listUserByRole(role));
    }

    // Outros endpoints de CRUD (GET by ID, PUT, DELETE)
    // podem ser adicionados aqui.

}