package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryEntityToCategoryDtoMapper extends BaseMapper<CategoryEntity, CategoryDto> {

    @Override
    CategoryDto map(CategoryEntity entity);
}
