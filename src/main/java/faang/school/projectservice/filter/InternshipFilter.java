package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;

import java.util.List;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filterDto);

    void apply(List<InternshipDto> list, InternshipFilterDto filterDto);
}