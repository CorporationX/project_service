package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;

import java.util.List;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filterDto);

    void apply(List<InternshipDto> list, InternshipFilterDto filterDto);
}
