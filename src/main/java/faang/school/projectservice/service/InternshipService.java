package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InternshipService {
    private final InternshipValidator validator;
    private final InternshipMapper internshipMapper;
    private final InternshipRepository internshipRepository;

    public InternshipDto create(InternshipDto internshipDto) {
        validator.validateForCreation(internshipDto);

        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipDto update(InternshipDto internshipDto) {
        validator.validateForUpdate(internshipDto);








        return null;
    }

}
