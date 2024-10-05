package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipFiltersIntStatusImpl implements InternshipFilters {
    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getInternshipStatus() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream.filter(internship -> internship.getStatus().equals(internshipFilterDto.getInternshipStatus()));
    }
}
