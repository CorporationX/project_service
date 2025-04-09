package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto internship);

    Stream<Internship> apply(Stream<Internship> stream,
                             InternshipFilterDto internship);
}
