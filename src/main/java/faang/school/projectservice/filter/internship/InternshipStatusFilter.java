package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.internshipStatus() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto internshipFilterDto) {
        return internships.filter(internship -> internship.getStatus().equals(internshipFilterDto.internshipStatus()));
    }
}
