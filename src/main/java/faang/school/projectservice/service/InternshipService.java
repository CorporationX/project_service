package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.TeamRoleDto;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(CreateInternshipDto internship);

    void updateInternship(Long internshipId, TeamRoleDto teamRole);

    List<InternshipDto> getAllInternshipsOnProject(Long projectId);

    InternshipDto getInternshipById(Long internshipId);

    List<InternshipDto> getAllInternships(InternshipFilterDto filters);
}
