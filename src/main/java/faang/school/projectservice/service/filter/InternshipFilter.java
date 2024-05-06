package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filters);
    Stream<Internship> apply(Supplier<Stream<Internship>> internships, InternshipFilterDto filters);
}
