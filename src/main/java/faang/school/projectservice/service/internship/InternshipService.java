package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipDtoMapper;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InternshipService {

    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final InternshipRepository internshipRepository;
    private final InternshipDtoMapper internshipDtoMapper;

    public InternshipDto createInternship(CreateInternshipDto createInternshipDto) {

        internshipValidator.validateInternshipLength(createInternshipDto);
        internshipValidator.validateMentorBelongsToProject(createInternshipDto);

        Internship internship = internshipMapper.toInternship(createInternshipDto);

        internship = internshipRepository.save(internship);

        return internshipDtoMapper.toInternshipDto(internship);
    }
}