package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidationTool validationTool;
    private final InternshipDataPreparer dataPreparer;

    public InternshipDto create(InternshipDto internshipDto) {
        validationTool.validationBeforeCreate(internshipDto);
        Internship entity = internshipMapper.toEntity(internshipDto);
        entity = dataPreparer.prepareEntityForCreate(internshipDto, entity);

        entity = internshipRepository.save(entity);
        log.info("The internship has been saved: {}", entity);

        return internshipMapper.toDto(entity);
    }

    public InternshipDto update(InternshipDto internshipDto) {
        Internship entity = findInternshipById(internshipDto.getId());

        boolean isCompleted = validationTool.validationStatus(internshipDto, entity);
        validationTool.validationBeforeUpdate(internshipDto, entity);

        if (isCompleted) {
            // Отсутствует информация, на какую роль идёт стажировка. Использую заглушку.
            TeamRole role = TeamRole.DEVELOPER;
            dataPreparer.evaluationInterns(entity, role);
        }

        entity = dataPreparer.prepareEntityForUpdate(internshipDto, entity);
        entity = internshipRepository.save(entity);
        log.info("The internship has been updated: {}", entity);

        return internshipMapper.toDto(entity);
    }

    public InternshipDto getInternship(Long internshipId) {
        return internshipMapper.toDto(findInternshipById(internshipId));
    }

    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    public List<InternshipDto> getFilteredInternships(InternshipFilterDto filters) {
        List<Internship> internships = internshipRepository.findAll();
        dataPreparer.filterInternships(internships, filters);

        return internships.stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    private Internship findInternshipById(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Стажировки с таким id не существует."));
    }
}
