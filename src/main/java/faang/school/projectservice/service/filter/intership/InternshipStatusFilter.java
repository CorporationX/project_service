package faang.school.projectservice.service.filter.intership;

import faang.school.projectservice.apimodel.InternshipFilterDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.filter.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class InternshipStatusFilter implements Filter<Internship, InternshipFilterDto> {

    private final InternshipMapper internshipMapper;


    @Override
    public boolean isApplicable(InternshipFilterDto dto) {
        return dto.getStatuses() != null && !dto.getStatuses().isEmpty();
    }

    @Override
    public Stream<Internship> filter(Stream<Internship> internships, InternshipFilterDto dto) {
        List<InternshipStatus> statuses = dto.getStatuses().stream()
                .map(internshipMapper::map)
                .toList();

        return internships.filter(internship -> statuses.contains(internship.getStatus()));
    }
}