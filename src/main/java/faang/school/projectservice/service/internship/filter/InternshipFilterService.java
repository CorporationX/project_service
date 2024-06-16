package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilterService {
    Stream<Internship> applyFilters(Stream<Internship> internships, InternshipFilterDto userFilterDto);
}