package faang.school.projectservice.filter;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Optional;

public interface FilterProject<T, U> {
    Optional<BooleanExpression> getCondition(T t, U u);
}