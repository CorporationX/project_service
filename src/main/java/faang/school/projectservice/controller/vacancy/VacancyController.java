package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.api.VacancyApi;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.UserService;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@RestController
@RequiredArgsConstructor
public class VacancyController implements VacancyApi {
    private final VacancyService vacancyService;
    private final VacancyValidator vacancyValidator;
    private final UserContext userContext;
    private final UserService userService;

    @Override
    public VacancyDto getVacancy(Long id) {
        return vacancyService.getVacancyDto(id);
    }

    @Override
    public List<VacancyDto> getVacancies(VacancyFilterDto filter) {
        return vacancyService.getVacancies(filter);
    }

    @Override
    public VacancyDto create(VacancyDto vacancyDto) {
        vacancyValidator.validateVacancy(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    @Override
    public VacancyDto update(VacancyDto vacancyDto) {
        long userId = userContext.getUserId();
        UserDto user = userService.getUserDtoById(userId);
        
        vacancyValidator.validateUser(user, vacancyDto);
        vacancyValidator.validateSupervisorRole(user.getId());
        vacancyValidator.validateVacancy(vacancyDto);

        return vacancyService.updateOrCloseVacancy(vacancyDto);
    }

    @Override
    public VacancyDto close(Long id) {
        long userId = userContext.getUserId();
        UserDto user = userService.getUserDtoById(userId);

        vacancyValidator.validateSupervisorRole(user.getId());
        return vacancyService.closeVacancy(id);
    }
}
