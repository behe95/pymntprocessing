package com.pymntprocessing.pymntprocessing.model.mapper;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class BaseMapper <E, D> {
    public abstract E convertToEntity(D dto);
    public abstract D convertToDTO(E entity);

    public List<E> convertToEntityList(Collection<D> dtos) {
        if (Objects.isNull(dtos)) {
            return List.of();
        }
        return dtos.stream().map(this::convertToEntity).toList();
    }

    public List<D> convertToDTOList(Collection<E> entities) {
        if (Objects.isNull(entities)) {
            return List.of();
        }
        return entities.stream().map(this::convertToDTO).toList();
    }
}
