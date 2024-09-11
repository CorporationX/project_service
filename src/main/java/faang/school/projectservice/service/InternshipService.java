package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipInfoDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(CreateInternshipDto internship);

    void updateInternship(Long internshipId, UpdateInternshipDto updatedInternship);

    List<InternshipDto> getAllInternshipsOnProject(Long projectId);

    InternshipInfoDto getInternshipById(Long internshipId);

    List<InternshipDto> getAllInternships();
}
