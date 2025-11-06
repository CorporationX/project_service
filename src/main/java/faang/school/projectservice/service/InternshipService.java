package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.SearchDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;

import java.util.List;

public interface InternshipService {

    InternshipDto createInternship(Long projectId, CreateInternshipDto internshipDto);

    InternshipDto updateInternship(Long internshipId, UpdateInternshipDto internshipDto);

    List<InternshipDto> findInternships(SearchDto searchDto);

    List<InternshipDto> findAll();

    InternshipDto findById(Long internshipId);
}
