package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(InternshipDto internshipDto);

    InternshipDto updateInternship(InternshipDto updatedInternshipDto);

    InternshipDto addNewIntern(long internshipId, long newInternId);

    InternshipDto finishInternshipForIntern(long internshipId, long internId, TeamRole teamRole);

    InternshipDto removeInternFromInternship(long internshipId, long internId);

    List<InternshipDto> getInternshipsByStatus(InternshipStatus status, InternshipFilterDto filters);

    List<InternshipDto> getInternshipsByRole(TeamRole role, InternshipFilterDto filters);

    List<InternshipDto> getAllInternships(InternshipFilterDto filters);
}
