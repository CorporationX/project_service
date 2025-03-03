package faang.school.projectservice.fillters;

import java.util.stream.Stream;

public interface Filter<Entity, Filter> {
    default Stream<Entity> apply(Stream<Entity> stream, Filter filters) {
        if (!isApplicable(filters)) {
            return stream;
        }
        return stream.filter(entity -> filterEntity(entity, filters));
    }

    boolean isApplicable(Filter filters);

    boolean filterEntity(Entity entity, Filter filters);
}
