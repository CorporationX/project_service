package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DateFilter implements InternshipFilter {

    @Override
    public boolean isAcceptable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getStartDate() != null && internshipFilterDto.getEndDate() != null;
    }

    @Override
    public Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto internshipFilterDto) {
        return internships.filter(internship -> !internship.getStartDate().isAfter(internship.getEndDate()));
    }
}
