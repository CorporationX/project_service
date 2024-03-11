package faang.school.projectservice.controller.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Internship", description = "The Internship API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipService internshipService;
    private final UserContext userContext;

    @Operation(
            summary = "Создаём стажировку",
            description = "Получает InternshipDto и создаёт стажировку"
    )
    @PostMapping
    public InternshipDto createInternship(@Valid @RequestBody InternshipDto internshipDto) {
        long userId = userContext.getUserId();
        internshipDto.setCreatedBy(userId);
        return internshipService.createInternship(internshipDto);
    }

    @Operation(
            summary = "Добавляем новых стажеров",
            description = "Получает id стажировки и стажера"
    )
    @PutMapping("/intern/{id}/add/{internshipId}")
    public InternshipDto addNewIntern(@PathVariable @Positive(message = "Id must be greater than zero") long internshipId,
                                      @PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return internshipService.addNewIntern(internshipId, id);
    }

    @Operation(
            summary = "Стажер заканчивает стажировку досрочно",
            description = "Получает id стажировки и стажера"
    )
    @PutMapping("/intern/{id}/finish/{internshipId}")
    public InternshipDto finishInternPrematurely(@PathVariable @Positive(message = "Id must be greater than zero") long internshipId,
                                                 @PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return internshipService.finishInternPrematurely(internshipId, id);
    }

    @Operation(
            summary = "Удаляем стажера досрочно",
            description = "Получает id стажировки и стажера"
    )
    @DeleteMapping("intern/{id}/delete/{internshipId}")
    public InternshipDto removeInternPrematurely(@PathVariable @Positive(message = "Id must be greater than zero") long internshipId,
                                                 @PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return internshipService.removeInternPrematurely(internshipId, id);
    }

    @Operation(
            summary = "Обновляем стажировку",
            description = "Получает InternshipDto с обязательным полем id и измененными полями"
    )
    @PutMapping
    public InternshipDto updateInternship(@NotNull @RequestBody InternshipDto internshipDto) {
        return internshipService.updateInternship(internshipDto);
    }

    @Operation(
            summary = "Стажировка завершена",
            description = "Получает id стажировки"
    )
    @PutMapping("/update/end/{internshipId}")
    public InternshipDto updateInternshipAfterEndDate(@PathVariable @Positive(message = "Id must be greater than zero") long internshipId) {
        return internshipService.updateInternshipAfterEndDate(internshipId);
    }

    @Operation(
            summary = "Получить все стажировки проекта с фильтрами по статусу"
    )
    @GetMapping("/filter/status")
    public List<InternshipDto> getInternshipByStatus(@RequestBody InternshipFilterDto filter) {
        return internshipService.getInternshipByStatus(filter);
    }

    @Operation(
            summary = "Получить все стажировки проекта с фильтрами по роли стажеров"
    )
    @GetMapping("/filter/{role}")
    public List<InternshipDto> getInternshipByRole(InternshipFilterDto id, @PathVariable TeamRole role) {
        return internshipService.getInternshipByRole(id, role);
    }

    @Operation(
            summary = "Получить все стажировки"
    )
    @GetMapping("/all")
    public List<InternshipDto> getAllInternship() {
        return internshipService.getAllInternship();
    }

    @Operation(
            summary = "Получить стажировку по id"
    )
    @GetMapping("/{id}")
    public InternshipDto getById(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return internshipService.getDtoById(id);
    }
}
