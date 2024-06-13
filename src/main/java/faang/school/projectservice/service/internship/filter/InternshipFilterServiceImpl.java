package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.model.Internship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipFilterServiceImpl implements InternshipFilterService {
    private final List<InternshipFilter> internshipFilters;

    @Override
    public Stream<Internship> applyFilters(Stream<Internship> internships, InternshipFilterDto internshipFilterDto) {
        if (internshipFilterDto != null) {
            for (InternshipFilter internshipFilter : internshipFilters) {
                if (internshipFilter.isAcceptable(internshipFilterDto)) {
                    internships = internshipFilter.applyFilter(internships, internshipFilterDto);
                }
            }
        }

        return internships;
    }
}
