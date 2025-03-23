package faang.school.projectservice.controller;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validations.annotations.IsCreateDataValid;
import faang.school.projectservice.validations.annotations.IsUpdateDataValid;
import jakarta.validation.Valid;
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
    @IsCreateDataValid
    public void createVacancy(@Parameter(
            name = "x-user-id",
            description = "ID user",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string"),
            example = "1"
    ) @RequestHeader("x-user-id") String userId, @Valid @RequestBody VacancyCreateDto vacancyDto) {
        userContext.setUserId(Long.parseLong(userId));
        Vacancy vacancy = createMapperToEntity(vacancyDto);
        log.info("Received request to create vacancy: {}", vacancy);
        service.createVacancy(vacancy);
        log.info("Vacancy created successfully: {}", vacancy);
    }

    @PutMapping("/update")
    @IsUpdateDataValid
    public void updateVacancy(@Parameter(
            name = "x-user-id",
            description = "ID user",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string"),
            example = "1"
    ) @RequestHeader("x-user-id") String userId, @Valid @RequestBody VacancyUpdateDto vacancyDto) {
        userContext.setUserId(Long.parseLong(userId));
        Vacancy existingVacancy = service.getVacancyById(vacancyDto.getId());
        Vacancy vacancy = updateMapperToEntity(existingVacancy, vacancyDto);
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
    public Vacancy createMapperToEntity(VacancyCreateDto vacancyDto) {
        Vacancy vacancy;
        return vacancy = mapper.toEntity(vacancyDto);
    }
    public Vacancy updateMapperToEntity(Vacancy vacancy, VacancyUpdateDto vacancyDto) {
        return vacancy = mapper.update(vacancy, vacancyDto);
    }

}
