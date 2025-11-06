package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.SearchDto;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;

import java.util.stream.Stream;

public class TestInternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(SearchDto searchDto) {
        return true;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, SearchDto searchDto) {
        return internships.filter(internship -> internship.getStatus() == InternshipStatus.COMPLETED);
    }
}
