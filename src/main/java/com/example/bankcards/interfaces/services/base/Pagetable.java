package com.example.bankcards.interfaces.services.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

public interface Pagetable<TEntity> {
    <T> Page<T> getPages(Specification<TEntity> specification, Pageable pageable, Function<TEntity, T> mapper);
}

