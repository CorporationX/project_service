package faang.school.projectservice.filter;

import faang.school.projectservice.dto.internship.SearchDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {

    boolean isApplicable(SearchDto searchDto);

    Stream<Internship> apply(Stream<Internship> internships, SearchDto searchDto);
}
