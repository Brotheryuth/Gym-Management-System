package com.gym.repository;

import java.util.List;

public interface Repository<T, ID> {
    boolean insert(T entity);
    boolean update(T entity);
    T findById(ID id);
    List<T> findAll();
    boolean delete(ID id);
}
