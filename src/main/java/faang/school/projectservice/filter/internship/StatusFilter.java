package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filter) {
        return (filter.getStatus() != null) && (filter.getStatus().equals(InternshipStatus.IN_PROGRESS)
                || filter.getStatus().equals(InternshipStatus.COMPLETED));
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> elements, InternshipFilterDto filter) {
        return elements.filter(internship -> internship.getStatus().equals(filter.getStatus()));
    }
}
