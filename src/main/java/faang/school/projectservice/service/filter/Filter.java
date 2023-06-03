package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.FilterDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Filter<T> {

    public List<T> applyFilter(Stream<T> stream, FilterDto filter) {
        if (isApplicable(filter)) {
            stream = stream.filter(element -> applyFilter(element, filter));
        }
        return stream
                .skip((long) filter.getPageSize() * filter.getPage())
                .limit(filter.getPageSize())
                .collect(Collectors.toList());
    }

    protected abstract boolean applyFilter(T element, FilterDto filter);

    protected abstract boolean isApplicable(FilterDto filter);
}