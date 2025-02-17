package faang.school.projectservice.service.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterRequest;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class RoleFilter implements InternshipFilter {
    @Override
    public Stream<Internship> filter(Stream<Internship> stream, InternshipFilterRequest request) {
        return request.roles() == null
                ? stream
                : stream.filter(internship -> request.roles().stream()
                .allMatch(role -> internship.getRole().equals(role)));
    }
}
