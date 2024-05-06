package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipServiceValidation internshipServiceValidation;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;

    public InternshipDto create(InternshipDto internshipDto) {
        internshipServiceValidation.validationCreate(internshipDto);

        return internshipMapper.toDto(internshipRepository.save(internshipMapper.toEntity(internshipDto)));
    }
}