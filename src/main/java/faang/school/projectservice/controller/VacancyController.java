package faang.school.projectservice.controller;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
@Validated
@Tag(name = "Vacancies", description = "Operations related to vacancies")
public class VacancyController {

    private final VacancyService vacancyService;
    private static final String ID_ERR_MESSAGE = "Vacancy id must be more than 0";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new vacancy", description = "Creates a new vacancy with the provided details")
    public VacancyDto create(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vacancy", description = "Updates an existing vacancy with the provided details")
    public VacancyDto update(@Valid @RequestBody VacancyDto vacancyDto,
                             @PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) Long id) {
        return vacancyService.update(vacancyDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a vacancy", description = "Deletes the vacancy with the specified ID")
    public void delete(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) Long id) {
        vacancyService.delete(id);
    }

    @GetMapping("/filter")
    @Operation(summary = "Get vacancies by filter", description = "Retrieves a list of vacancies that match the specified filter criteria")
    public List<VacancyDto> getAllByFilter(@Valid @RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getAllByFilter(vacancyFilterDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find vacancy by ID", description = "Retrieves the details of a vacancy by its ID")
    public VacancyDto findByID(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) Long id) {
        return vacancyService.findById(id);
    }
}