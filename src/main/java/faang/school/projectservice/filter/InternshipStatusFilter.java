package faang.school.projectservice.filter;

import faang.school.projectservice.dto.internship.SearchDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(SearchDto searchDto) {
        return searchDto.status() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, SearchDto searchDto) {
        return internships.filter(i -> i.getStatus().equals(searchDto.status()));
    }
}
