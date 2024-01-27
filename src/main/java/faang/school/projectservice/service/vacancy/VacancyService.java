package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectService projectService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        projectService.existsProjectById(vacancyDto.getProjectId());
        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);

        vacancyRepository.save(newVacancy);

        return vacancyMapper.toDto(newVacancy);
    }
}
