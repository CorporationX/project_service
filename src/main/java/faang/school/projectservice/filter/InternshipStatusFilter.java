package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filterDto) {
        return filterDto.getInternshipStatus() != null;
    }

    @Override
    public void apply(List<InternshipDto> list, InternshipFilterDto filterDto) {
        list.removeIf(dto -> !dto.getInternshipStatus().equals(filterDto.getInternshipStatus()));
    }
}
