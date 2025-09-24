package com.example.bankcards.interfaces.services.base;

import java.util.function.Function;

public interface Creatable<TDtoCreate, TEntity> {
    <T> T create(TDtoCreate dto, Function<TEntity, T> mapper);
}
