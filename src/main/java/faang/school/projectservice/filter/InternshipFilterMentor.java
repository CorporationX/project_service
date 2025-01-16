package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.Objects;
import java.util.stream.Stream;

public class InternshipFilterMentor implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getMentorId() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> requests, InternshipFilterDto filters) {
        return requests.filter(request -> Objects.equals(request.getMentorId().getId(), filters.getMentorId()));
    }
}
