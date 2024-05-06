package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Internship> apply(Supplier<Stream<Internship>> internships, InternshipFilterDto filters) {
        return internships.get().filter(internship -> filters.getStatus().equals(internship.getStatus()));
    }
}
