package br.edu.ifpb.veritas.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Service<T, ID> {

    public List<T> findAll();

    public Page<T> findAll(Pageable p);

    public T findById(ID id);

    public T save(T t);
    
}
