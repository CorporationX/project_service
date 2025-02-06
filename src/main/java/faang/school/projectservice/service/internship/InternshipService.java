package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(InternshipDto internshipDto);

    InternshipDto updateInternship(InternshipUpdateDto internshipUpdateDto);

    List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters);

    List<InternshipDto> getAllInternships();

    InternshipDto getInternship(Long id);
}
