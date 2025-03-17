package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;

import java.util.stream.Stream;

public class TestInternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto internship) {
        return true;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> stream, InternshipFilterDto internship) {
        return stream.filter(internship2 -> internship2.getStatus().equals(InternshipStatus.COMPLETED));
    }
}
