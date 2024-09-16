package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;

import java.util.List;

public interface InternshipService {
    InternshipDto createInternship(InternshipDto internshipDto);

    List<InternshipDto> getAllInternships();

    InternshipDto updateInternship(long id, InternshipDto internshipDto);

    List<InternshipDto> getAllInternshipsByFilter(InternshipFilterDto filter);

    InternshipDto getInternshipById(long id);
}
