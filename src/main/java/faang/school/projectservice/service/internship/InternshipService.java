package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.dto.intership.InternshipFilterDto;

import java.util.List;

public interface InternshipService {

    InternshipDto create(InternshipDto internshipDto);

    InternshipDto update(InternshipDto internshipDto);

    List<InternshipDto> getInternshipByFilter(InternshipFilterDto filters);

    List<InternshipDto> getAllInternships(InternshipDto internshipDto);

    InternshipDto getInternshipById(InternshipDto internshipDto);
}
