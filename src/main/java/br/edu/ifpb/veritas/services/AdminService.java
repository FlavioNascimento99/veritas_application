package br.edu.ifpb.veritas.services;

import br.edu.ifpb.veritas.exceptions.ResourceNotFoundException;
import br.edu.ifpb.veritas.models.Administrator;
import br.edu.ifpb.veritas.repositories.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Fiz um CRUD básico, mas precisa rever
@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Transactional
    public Administrator create(Administrator admin) {
        if (admin.getLogin() != null && adminRepository.findByLogin(admin.getLogin()).isPresent()) {
            throw new ResourceNotFoundException("Login já cadastrado.");
        }
        if (admin.getRegister() != null && adminRepository.findByRegister(admin.getRegister()).isPresent()) {
            throw new ResourceNotFoundException("Matrícula já cadastrada.");
        }
        return adminRepository.save(admin);
    }

    public List<Administrator> findAll() {
        return adminRepository.findAll();
    }

    public Administrator findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado."));
    }

    @Transactional
    public Administrator update(Long id, Administrator payload) {
        Administrator current = findById(id);
        current.setName(payload.getName());
        current.setPhoneNumber(payload.getPhoneNumber());
        current.setLogin(payload.getLogin());
        current.setPassword(payload.getPassword());
        current.setRegister(payload.getRegister());
        return adminRepository.save(current);
    }

    @Transactional
    public void delete(Long id) {
        Administrator current = findById(id);
        adminRepository.delete(current);
    }
}