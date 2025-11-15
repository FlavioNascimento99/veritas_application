package br.edu.ifpb.veritas.controllers.api;

//import br.edu.ifpb.veritas.DTOs.userDTO.UserCreateDTO;
//import br.edu.ifpb.veritas.DTOs.userDTO.UserDTO;
import br.edu.ifpb.veritas.enums.UserRole;
import br.edu.ifpb.veritas.models.User;
import br.edu.ifpb.veritas.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAPIController {

    private final UserService userService;

    // REQFUNC 14: CRUD para alunos, professores e coordenadores
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> listAllUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @GetMapping("/by-role")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    public ResponseEntity<List<User>> listUsersByRole(@RequestParam UserRole role) {
        return ResponseEntity.ok(userService.listUserByRole(role));
    }

//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
//        UserDTO newUser = userService.createUser(userCreateDTO);
//        // Idealmente, retornaria um status 201 Created com a URI do novo recurso
//        return ResponseEntity.ok(newUser);
//    }
//
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UserDTO>> listAllUsers() {
//        return ResponseEntity.ok(userService.listUsers());
//    }
//
//    // Endpoint útil para o Coordenador listar professores (REQFUNC 8)
//    // e para o Admin listar usuários por papel.
//    @GetMapping("/by-role")
//    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
//    public ResponseEntity<List<UserDTO>> listUsersByRole(@RequestParam UserRole role) {
//        return ResponseEntity.ok(userService.listUsersByRole(role));
//    }

    //TODO: Outros endpoints de CRUD (GET by ID, PUT, DELETE) podem ser adicionados aqui.

}