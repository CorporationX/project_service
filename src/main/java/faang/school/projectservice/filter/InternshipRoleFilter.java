package faang.school.projectservice.filter;

import faang.school.projectservice.dto.internship.SearchDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(SearchDto searchDto) {
        return searchDto.role() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, SearchDto searchDto) {
        return internships.filter(i -> i.getRole().equals(searchDto.role()));
    }
}
