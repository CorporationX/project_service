package faang.school.projectservice.filter.impl;

import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.model.dto.InternshipFilterDto;
import faang.school.projectservice.model.entity.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getStatusPattern() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream.filter(internship -> internship.getStatus() == internshipFilterDto.getStatusPattern());
    }
}
