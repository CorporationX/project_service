package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.internship.InternshipDto;
import faang.school.projectservice.model.dto.internship.InternshipFilterDto;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(InternshipDto internshipDto);

    List<InternshipDto> getAllInternships();

    InternshipDto updateInternship(long id, InternshipDto internshipDto);

    List<InternshipDto> getAllInternshipsByFilter(InternshipFilterDto filter);

    InternshipDto getInternshipById(long id);
}
