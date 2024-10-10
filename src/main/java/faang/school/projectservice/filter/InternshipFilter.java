package faang.school.projectservice.filter;

import faang.school.projectservice.model.dto.InternshipFilterDto;
import faang.school.projectservice.model.entity.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto internshipFilterDto);

    Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto);
}
