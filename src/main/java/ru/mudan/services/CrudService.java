package ru.mudan.services;

import java.util.List;

public interface CrudService<T>{
    List<T> findAll();
    T findById(Long id);
    void save(T request);
    void update(T request, Long id);
    void deleteById(Long id);
}
