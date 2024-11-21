package ru.mudan.facade;

/**
 * Интерфейс с методом конвертации сущности в DTO
 *
 * @param <E> - тип сущности
 * @param <D> - тип DTO
 */
public interface BaseFacade<E, D> {
    D convertEntityToDTO(E entity);
}
