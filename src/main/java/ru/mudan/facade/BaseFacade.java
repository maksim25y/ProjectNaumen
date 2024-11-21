package ru.mudan.facade;

public interface BaseFacade<E, D> {
    D convertEntityToDTO(E entity);
}
