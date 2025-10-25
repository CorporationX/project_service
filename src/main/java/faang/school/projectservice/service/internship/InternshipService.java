package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;

import java.util.List;

public interface InternshipService {
    InternshipDto create(CreateInternshipDto createInternshipDto);

    InternshipDto update(long goalId, UpdateInternshipDto updateInternshipDto);

    List<InternshipDto> getByFilters(InternshipFilterDto internshipFilterDto);

    List<InternshipDto> getAll();

    InternshipDto getById(long internshipId);
}