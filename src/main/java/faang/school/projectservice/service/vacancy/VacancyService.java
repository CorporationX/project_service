package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyValidator validator;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final VacancyRepository vacancyRepository;
    private final UserContext userContext;

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        long creator = userContext.getUserId();
        validator.createVacancyValidation(vacancyDto, creator);

        Project vacancyProject = projectRepository.getProjectById(vacancyDto.getProjectId());

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setProject(vacancyProject);
        vacancy.setCreatedBy(creator);
        vacancy.setUpdatedBy(creator);
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setUpdatedAt(LocalDateTime.now());

        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }
}
