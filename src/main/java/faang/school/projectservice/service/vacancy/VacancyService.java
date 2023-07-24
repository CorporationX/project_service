package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.PROJECT_NOT_EXIST_FORMAT;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;

    @Transactional
    public VacancyDto createVacancy(@Valid VacancyDto vacancyDto) {
        // вакансия всегда относиться к какому-то проекту
        // -> нужно проверить существует ли проект с таким ид
        // значит надо сходить в БД и проверить что такой проект существует
        checkProjectExist(vacancyDto.getProjectId());
        Vacancy newVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(newVacancy);
    }

    private void checkProjectExist(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            String errorMessage = MessageFormat.format(PROJECT_NOT_EXIST_FORMAT, projectId);
            throw new VacancyValidateException(errorMessage);
        }
    }
}
