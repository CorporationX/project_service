package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateDto;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(InternshipDto internshipDto);

    InternshipUpdateDto updateInternship(InternshipUpdateDto internshipUpdateDto);

    List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters);

    List<InternshipDto> getAllInternships();

    InternshipDto getInternship(Long id);
}
