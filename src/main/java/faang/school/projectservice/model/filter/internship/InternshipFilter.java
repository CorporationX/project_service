package faang.school.projectservice.model.filter.internship;

import faang.school.projectservice.model.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.entity.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto internship);

    Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto filters);
}
