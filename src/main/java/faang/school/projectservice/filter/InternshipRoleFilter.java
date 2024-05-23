package faang.school.projectservice.filter;

import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public Stream<Internship> apply(Supplier<Stream<Internship>> internships, InternshipFilterDto filters) {
        return internships.get().filter(internship -> filters.getName().equals(internship.getName()));
    }
}
