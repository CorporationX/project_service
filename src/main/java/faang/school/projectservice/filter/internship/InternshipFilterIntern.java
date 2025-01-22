package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class InternshipFilterIntern implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
//        return filters.getInternId() != null;
        return false;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> requests, InternshipFilterDto filters) {
//        flatmap? TODO
//        var res = requests.flatMap(internship -> internship.getInterns().stream()).filter(intern->intern.getUserId() == filters.getInternId());
        return Stream.of();
    }
}
