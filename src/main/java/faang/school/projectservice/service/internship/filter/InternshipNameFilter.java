package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
class InternshipNameFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        String internshipName = filters.getNamePattern();
        return internshipName != null && !internshipName.isBlank();
    }

    @Override
    public Stream<Internship> apply(List<Internship> internships, InternshipFilterDto filters) {
        return internships.stream()
                .filter(internship -> internship.getName().contains(filters.getNamePattern()));
    }
}