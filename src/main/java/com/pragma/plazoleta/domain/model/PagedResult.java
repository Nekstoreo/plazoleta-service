package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResult<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static <T> PagedResult<T> of(List<T> content, int page, int size, 
                                         long totalElements, int totalPages) {
        PagedResult<T> result = new PagedResult<>();
        result.setContent(content);
        result.setPage(page);
        result.setSize(size);
        result.setTotalElements(totalElements);
        result.setTotalPages(totalPages);
        result.setFirst(page == 0);
        result.setLast(page >= totalPages - 1);
        return result;
    }
}
