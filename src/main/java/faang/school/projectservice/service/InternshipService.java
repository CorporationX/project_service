package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;

import java.util.List;

public interface InternshipService {
    void createInternship(InternshipDto internshipDto);

    InternshipDto updateInternship(Long id);

    List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters);

    List<InternshipDto> getAllInternships();

    InternshipDto getInternship(Long id);
}
