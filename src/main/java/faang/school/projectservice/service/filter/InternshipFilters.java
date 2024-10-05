package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilters {

    boolean isApplicable(InternshipFilterDto internshipFilterDto);

    Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto);
}
