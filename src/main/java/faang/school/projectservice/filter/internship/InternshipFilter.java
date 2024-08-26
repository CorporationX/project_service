package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto internship);
    Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto internship);
}