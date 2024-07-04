package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NULL_INTERNSHIP_ID_EXCEPTION;

@RequestMapping("/internship")
@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipControllerValidation internshipControllerValidation;


    @Operation(summary = "Создание стажировки", tags = {"internship"})
    @ApiResponse(responseCode = "201", description = "Стажировка успешно создана")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PostMapping
    public InternshipDto create(@Valid @RequestBody InternshipDto internshipDto) {
        internshipControllerValidation.validateInternshipDuration(internshipDto);
        internshipControllerValidation.validateInternshipDates(internshipDto);

        return internshipService.create(internshipDto);
    }
    @Operation(summary = "Обновление стажировки", tags = {"internship"})
    @ApiResponse(responseCode = "200", description = "Стажировка успешно обновлена")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PutMapping
    public InternshipDto update(@Valid @RequestBody InternshipDto internshipDto) {
        internshipControllerValidation.validateInternshipDuration(internshipDto);

        if (internshipDto.getId() == null) {
            throw new DataValidationException(NULL_INTERNSHIP_ID_EXCEPTION.getMessage());
        }

        return internshipService.update(internshipDto);
    }
    @Operation(summary = "Получение стажировок из проекта", tags = {"internship"})
    @ApiResponse(responseCode = "200", description = "Стажировка из проекта успешно получена")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PostMapping(value = "/project/{projectId}")
    public List<InternshipDto> getInternshipsOfProject(@PathVariable Long projectId,
                                                       @RequestBody InternshipFilterDto filter) {
        return internshipService.getInternshipsOfProject(projectId, filter);
    }

    @Operation(summary = "Получение всех стажировок", tags = {"internship"})
    @ApiResponse(responseCode = "200", description = "Все стажировки успешно получены")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @GetMapping
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @Operation(summary = "Удаление стажировок согласно id", tags = {"internship"})
    @ApiResponse(responseCode = "200", description = "Стажировка успешно удалена")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @GetMapping("/{internshipId}")
    public InternshipDto getInternshipById(@PathVariable Long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}
