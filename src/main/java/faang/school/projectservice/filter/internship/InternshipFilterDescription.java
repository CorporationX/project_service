package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class InternshipFilterDescription implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
//        return filters.getDescription() != null;
        return false;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> requests, InternshipFilterDto filters) {
//        return requests.filter(request -> request.getDescription().contains(filters.getDescription()));
    return Stream.of();
    }
}
