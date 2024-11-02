package ru.mudan.services;

import java.util.List;

public interface CrudService<T>{
    List<T> findAll();
    T findById(Long id);
}
