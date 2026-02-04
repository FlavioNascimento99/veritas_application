package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.models.Administrator;
import br.edu.ifpb.veritas.models.Professor;
import br.edu.ifpb.veritas.models.Student;
import br.edu.ifpb.veritas.repositories.AdminRepository;
import br.edu.ifpb.veritas.repositories.ProfessorRepository;
import br.edu.ifpb.veritas.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Administrator> admin = adminRepository.findByLogin(username);
        if (admin.isPresent()) {
            Administrator user = admin.get();
            return User.builder()
                    .username(user.getLogin())
                    .password(user.getPassword())
                    .disabled(!user.getIsActive())
                    .roles("ADMIN")
                    .build();
        }

        Optional<Professor> professor = professorRepository.findByLogin(username);
        if (professor.isPresent()) {
            Professor user = professor.get();
            User.UserBuilder builder = User.builder()
                    .username(user.getLogin())
                    .password(user.getPassword())
                    .disabled(!user.getIsActive());

            if (user.getCoordinator()) {
                builder.roles("PROFESSOR", "COORDINATOR");
            } else {
                builder.roles("PROFESSOR");
            }
            return builder.build();
        }

        return studentRepository.findByLogin(username)
                .map(student -> User.builder()
                        .username(student.getLogin())
                        .password(student.getPassword())
                        .disabled(!student.getIsActive())
                        .roles("STUDENT")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + username));
    }
}