package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filters);

    Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto filters);
}
