package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(long userId, InternshipDto internshipDto);

    InternshipDto updateInternship(long internshipId, InternshipDto updatedInternshipDto);

    InternshipDto addNewIntern(long internshipId, long newInternId);

    InternshipDto finishInternshipForIntern(long internshipId, long internId, String teamRole);

    InternshipDto removeInternFromInternship(long internshipId, long internId);

    List<InternshipDto> getInternshipsByFilter(InternshipFilterDto filterDto);

    List<InternshipDto> getAllInternships();

    InternshipDto getInternshipById(long id);
}
