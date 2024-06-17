package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {

    boolean isAcceptable(InternshipFilterDto internshipFilterDto);

    Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto internshipFilterDto);
}