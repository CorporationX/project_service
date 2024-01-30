package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto internshipFilterDto);

    Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto);
}
