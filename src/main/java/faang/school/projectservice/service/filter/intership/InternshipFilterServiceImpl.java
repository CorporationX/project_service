package faang.school.projectservice.service.filter.intership;

import faang.school.projectservice.apimodel.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipFilterServiceImpl implements FilterService<Internship, InternshipFilterDto> {

    private final List<Filter<Internship, InternshipFilterDto>> filters;

    @Override
    public List<Internship> getFilteredList(List<Internship> entities, InternshipFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}