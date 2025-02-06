package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class InternshipProjectFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getProjectId() != 0;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> requests, InternshipFilterDto filters) {
        return requests.filter(request -> Objects.equals(request.getProject().getId(), filters.getProjectId()));
    }
}
