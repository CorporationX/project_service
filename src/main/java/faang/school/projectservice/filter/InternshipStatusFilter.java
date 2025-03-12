package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;
import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> stream, InternshipFilterDto internshipFilterDto) {
        return stream.filter(internship
                -> internshipFilterDto.getStatus().equals(internship.getStatus()));
    }
}
