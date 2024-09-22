package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class InternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.status() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internship.getStatus().equals(filters.status()));
    }
}