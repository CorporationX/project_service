package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.InternshipDto;
import faang.school.projectservice.model.dto.InternshipFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InternshipService {
    InternshipDto create(InternshipDto internshipDto);

    InternshipDto update(long id, InternshipDto internshipDto);

    List<InternshipDto> getInternshipsByProjectAndFilter(Long projectId, InternshipFilterDto filterDto);

    Page<InternshipDto> getAllInternships(Pageable pageable);

    InternshipDto getInternshipById(Long internshipId);
}
