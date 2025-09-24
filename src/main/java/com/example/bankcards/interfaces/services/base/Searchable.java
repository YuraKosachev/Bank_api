package com.example.bankcards.interfaces.services.base;

import java.util.UUID;
import java.util.function.Function;

public interface Searchable<TEntity> {
    <T> T findById(UUID id, Function<TEntity,T> mapper);
}
