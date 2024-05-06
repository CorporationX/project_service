package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validation.InternshipValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipValidation internshipValidation;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;

    public InternshipDto create(InternshipDto internshipDto) {
        internshipValidation.validationCreate(internshipDto);

        return internshipMapper.toDto(internshipRepository.save(internshipMapper.toEntity(internshipDto)));
    }
}