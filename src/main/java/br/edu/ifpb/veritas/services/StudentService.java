package br.edu.ifpb.veritas.services;

import java.util.List;
import br.edu.ifpb.veritas.models.Student;

public interface StudentService {
    Student create(Student student);
    Student update(Long id, Student student);
    void delete(Long id);
    Student findById(Long id);
    List<Student> findAll();
}
