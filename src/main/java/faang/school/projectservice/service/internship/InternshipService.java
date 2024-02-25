package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(InternshipDto internshipDto);

    InternshipDto addNewIntern(long internshipId, long teamMemberId);

    InternshipDto finishInternPrematurely(long internshipId, long teamMemberId);

    InternshipDto removeInternPrematurely(long internshipId, long internId);

    InternshipDto updateInternship(InternshipDto updatedInternshipDto);

    InternshipDto updateInternshipAfterEndDate(long internshipId);

    List<InternshipDto> getInternshipByStatus(InternshipFilterDto filter);

    List<InternshipDto> getInternshipByRole(InternshipFilterDto id, TeamRole role);

    List<InternshipDto> getAllInternship();

    InternshipDto getById(long id);

}
