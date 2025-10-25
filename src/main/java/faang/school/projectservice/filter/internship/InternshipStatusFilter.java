package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto filtersDto) {
        return filtersDto.status() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filtersDto) {
        return internships.filter(internship -> internship.getStatus().equals(filtersDto.status()));
    }
}