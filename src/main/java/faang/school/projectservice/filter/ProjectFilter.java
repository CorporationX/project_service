package faang.school.projectservice.filter;

import java.util.List;

public interface ProjectFilter <R, U> {
    boolean isApplicable (U u);
    void apply(List<R> r, U u);
}