package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.cart.model.VersionMismatch;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.VersionMismatchDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VersionMismatchDtoToVersionMismatchMapper extends BaseMapper<VersionMismatchDto, VersionMismatch> {

    @Override
    VersionMismatch map(VersionMismatchDto source);
}
