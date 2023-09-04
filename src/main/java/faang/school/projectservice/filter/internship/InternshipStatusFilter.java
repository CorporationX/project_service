package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public void apply(List<InternshipDto> list, InternshipFilterDto filterDto) {
        list.removeIf((dto) -> !dto.getStatus().equals(filterDto.getStatus()));
    }
}
