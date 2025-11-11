package faang.school.projectservice.dto.common;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        SortDto sort
) {

    public static <T, U> PageResponse<U> from(Page<T> page, Function<T, U> mapper) {
        return new PageResponse<>(
                page.map(mapper).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                SortDto.from(page.getSort())
        );
    }
}

