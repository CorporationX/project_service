package faang.school.projectservice.filter.internship;

import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filters);

    Stream<Internship> apply(Stream<Internship> internship, InternshipFilterDto filters);

}
