package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.OpenVacancyRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final OpenVacancyRequestValidator openVacancyRequestValidator;
    private final VacancyMapper vacancyMapper;

    public void openVacancy(OpenVacancyRequestDto requestDto) {
        var project = openVacancyRequestValidator.validateProject(requestDto);
        var author = openVacancyRequestValidator.validateAuthor(requestDto);
        openVacancyRequestValidator.validateSalary(requestDto);

        var vacancy = vacancyMapper.toVacancy(requestDto);
        vacancy.setCreatedBy(author.getId());
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);

        vacancyRepository.save(vacancy);
    }
}
