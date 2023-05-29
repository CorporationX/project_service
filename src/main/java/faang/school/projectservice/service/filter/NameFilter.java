package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.FilterDto;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class NameFilter extends Filter<String> {

    @Override
    protected boolean applyFilter(String name, FilterDto filter) {
        return name.matches(filter.getNamePattern());
    }

    @Override
    protected boolean isApplicable(FilterDto filter) {
        return nonNull(filter.getNamePattern());
    }
}