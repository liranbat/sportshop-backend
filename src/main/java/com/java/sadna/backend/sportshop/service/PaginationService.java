package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginationService {

    private int safePage(Integer page) {
        if (page == null) return 0;
        if (page < 0) {
            throw new BadRequestException("pagination.pageNegative");
        }
        return page;
    }

    private int safePageSize(Integer pageSize, int defaultSize) {
        if (pageSize == null) return defaultSize;
        if (pageSize < 1) {
            throw new BadRequestException("pagination.pageSizeMin");
        }
        return pageSize;
    }

    public <E, D> PagedResult<D> paginate(JpaSpecificationExecutor<E> repository,
                                          Specification<E> spec,
                                          Sort sort,
                                          Integer page,
                                          Integer pageSize,
                                          int defaultPageSize,
                                          BaseMapper<E, D> entityToDto) {
        int safePage = safePage(page);
        int safePageSize = safePageSize(pageSize, defaultPageSize);
        Pageable pageable = PageRequest.of(safePage, safePageSize, sort);
        Page<E> entityPage = repository.findAll(spec, pageable);
        ensurePageInRange(safePage, entityPage.getTotalPages());
        List<D> dtoItems = entityPage.getContent().stream().map(entityToDto::map).toList();
        return new PagedResult<>(
                dtoItems,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    private void ensurePageInRange(int page, int totalPages) {
        if (page > 0 && page >= totalPages) {
            throw new NotFoundException("pagination.outOfRange", page, totalPages);
        }
    }
}
