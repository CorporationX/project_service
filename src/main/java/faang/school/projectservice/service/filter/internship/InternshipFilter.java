package faang.school.projectservice.service.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterRequest;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    Stream<Internship> filter(Stream<Internship> stream, InternshipFilterRequest request);
}
