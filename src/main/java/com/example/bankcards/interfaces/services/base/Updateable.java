package com.example.bankcards.interfaces.services.base;

import java.util.function.Function;

public interface Updateable<TDtoCreate, TEntity> {
    <T> T update(TDtoCreate dto, Function<TEntity, T> mapper);
}
