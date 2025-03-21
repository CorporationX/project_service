package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto internshipDto) {
        return internshipDto.role() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> stream, InternshipFilterDto internshipDto) {
        return stream
                .filter(internship -> internshipDto.role().equals(internship.getRole()));
    }
}
