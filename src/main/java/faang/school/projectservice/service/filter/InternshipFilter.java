package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto internshipFilterDto);
    Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto);
}
