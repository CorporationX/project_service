package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.InternshipDto;
import faang.school.projectservice.model.dto.InternshipFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InternshipService {
    @Transactional
    InternshipDto create(InternshipDto internshipDto);

    @Transactional
    InternshipDto update(long id, InternshipDto internshipDto);

    @Transactional(readOnly = true)
    List<InternshipDto> getInternshipsByProjectAndFilter(Long projectId, InternshipFilterDto filterDto);

    Page<InternshipDto> getAllInternships(Pageable pageable);

    InternshipDto getInternshipById(Long internshipId);
}
