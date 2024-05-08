package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NON_EXISTING_INTERNSHIP_EXCEPTION;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipServiceValidation internshipServiceValidation;
    private final InternshipServiceUtility internshipServiceUtility;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> filters;


    public InternshipDto create(InternshipDto internshipDto) {
        internshipServiceValidation.validationCreate(internshipDto);

        Internship internship = internshipServiceUtility.toEntity(internshipDto);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipDto update(InternshipDto internshipDto) {
        internshipServiceValidation.validationUpdate(internshipDto);

        Internship internship = internshipServiceUtility.toEntity(internshipDto);

        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            internshipServiceUtility.processCompletedInternship(internship);
        }

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public List<InternshipDto> getInternshipsOfProject(long projectId, InternshipFilterDto filtersDto) {
        List<Internship> projectInternships = internshipRepository.findAll().stream()
                .filter(internship -> internship.getProject().getId().equals(projectId))
                .toList();

        return filters.stream()
                .filter(filter -> filter.isApplicable(filtersDto))
                .flatMap(filter -> filter.apply(projectInternships, filtersDto))
                .distinct()
                .map(internshipMapper::toDto)
                .toList();
    }

    public List<InternshipDto> getAllInternships() {
        return internshipMapper.toDto(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_INTERNSHIP_EXCEPTION.getMessage()));

        return internshipMapper.toDto(internship);
    }
}