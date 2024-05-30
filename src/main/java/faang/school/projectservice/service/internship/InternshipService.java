package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipToCreateDto;
import faang.school.projectservice.dto.internship.InternshipToUpdateDto;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(long userId, InternshipToCreateDto internshipDto);

    InternshipDto updateInternship(long userId, long internshipId, InternshipToUpdateDto updatedInternshipDto);

    List<InternshipDto> getAllInternshipsByProjectId(long projectId, InternshipFilterDto filterDto);

    List<InternshipDto> getAllInternships(InternshipFilterDto filter);

    InternshipDto getInternshipById(long id);
}
