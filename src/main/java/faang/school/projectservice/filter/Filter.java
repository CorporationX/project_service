package faang.school.projectservice.filter;

import java.util.stream.Stream;

public interface Filter<FilterDto, Entity> {
    boolean isApplicable(FilterDto filterDto);
    Stream<Entity> apply(Stream<Entity> items, FilterDto filterDto);
}