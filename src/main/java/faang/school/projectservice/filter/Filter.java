package faang.school.projectservice.filter;

import java.util.List;

public interface Filter<E, F> {

    boolean isApplicable(F filter);

    List<E> apply(List<E> collection, F filters);
}
