package faang.school.projectservice.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.QProject;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SubProjectStatusFilter implements FilterProject<FilterSubProjectDto, QProject> {

    @Override
    public Optional<BooleanExpression> getCondition(FilterSubProjectDto filter, QProject qProject) {
        if (filter.status() != null) {
            return Optional.of(qProject.status.eq(filter.status()));
        }
        return Optional.empty();
    }
}