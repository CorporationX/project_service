package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.Objects;
import java.util.stream.Stream;

public class InternshipFilterStatus implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
//        return filters.getStatus() != null;
        return false;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> requests, InternshipFilterDto filters) {
//        return requests.filter(request -> Objects.equals(request.getStatus(), filters.getStatus()));
        return Stream.of();
    }
}
