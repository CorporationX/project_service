package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService service;
    private final VacancyMapper mapper;
    private final UserContext userContext;

    @PostMapping("/create")
    public void createVacancy(@Parameter(
            name = "x-user-id",
            description = "ID user",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string"),
            example = "1"
    ) @RequestHeader("x-user-id") String userId, @RequestBody VacancyDto vacancyDto) {
        userContext.setUserId(Long.parseLong(userId));
        vacancyDto = isDataValid(vacancyDto);
        Vacancy vacancy = mapperToEntity(vacancyDto);
        log.info("Received request to create vacancy: {}", vacancy);
        service.createVacancy(vacancy);
        log.info("Vacancy created successfully: {}", vacancy);
    }

    @PutMapping("/update")
    public void updateVacancy(@Parameter(
            name = "x-user-id",
            description = "ID user",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string"),
            example = "1"
    ) @RequestHeader("x-user-id") String userId, @RequestBody VacancyDto vacancyDto) {
        userContext.setUserId(Long.parseLong(userId));
        vacancyDto = isDataValid(vacancyDto);
        Vacancy vacancy = mapperToEntity(vacancyDto);
        log.info("Received request to update vacancy: {}", vacancy);
        service.updateVacancy(vacancy);
        log.info("Vacancy updated successfully: {}", vacancy);
    }

    @DeleteMapping("/{vacancyId}")
    public void deleteVacancy(@PathVariable (value = "vacancyId", required = false) Long vacancyId) {
        log.info("Received request to delete vacancy with ID: {}", vacancyId);
        service.deleteVacancy(vacancyId);
        log.info("Vacancy deleted successfully: {}", vacancyId);
    }

    public VacancyDto isDataValid(VacancyDto vacancyDto) {
        if (vacancyDto.getPositionId() == null
                || vacancyDto.getProjectId() == null
                || vacancyDto.getRoleId() == null) {
            throw new NullPointerException("You are use illegal data: position and project must be not null");
        } else if (vacancyDto.getRoleId() != TeamRole.getAll().get(0).ordinal()
                && vacancyDto.getRoleId() != TeamRole.getAll().get(1).ordinal()) {
            throw new IllegalArgumentException("You are use illegal data: curator must be OWNER or MANAGER");
        }
        return vacancyDto;
    }
    public Vacancy mapperToEntity(VacancyDto vacancyDto) {
        Vacancy vacancy;
        return vacancy = mapper.toEntity(vacancyDto);
    }
}

