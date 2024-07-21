package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.InternshipFiltersDto;
import faang.school.projectservice.model.Internship;

import java.util.List;
import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFiltersDto filters);

    Stream<Internship> apply(Stream<Internship> internships, InternshipFiltersDto internshipFiltersDto);
}