package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Vacancies",
        description = "Managing project vacancies"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies")
public class VacancyController {
    final VacancyServiceImpl vacancyService;

    @Operation(
            summary = "Create a new vacancy",
            description = "Create a new vacancy in the project",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Vacancy successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VacancyDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\":"
                                            + " \"User has incorrect role for this action (create)\"}")
                            )
                    )
            }
    )
    @PostMapping
    VacancyDto createVacancy(
            @RequestBody
            @Parameter(description = "Data for creating a new vacancy")
            CreateVacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @Operation(
            summary = "Update vacancy by Id",
            description = "Update the vacancy fields by it's Id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vacancy successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VacancyDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Vacancy not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{\"error\":"
                                                    + "\"Vacancy not found\"}"))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Not enough candidates",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{\"error\":"
                                                    + " \"Not found enough candidates for the vacancy\"}"))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "No permission",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{\"error\":"
                                                    + " \"User has incorrect role for this action (update)\"}")))
            }
    )
    @PutMapping("/update/{vacancyId}")
    VacancyDto updateVacancy(
            @PathVariable
            @Parameter(description = "Id of the vacancy to update", example = "1")
            long vacancyId,
            @RequestBody
            @Parameter(description = "Updating data the vacancy")
            UpdateVacancyDto vacancyDto) {
        return vacancyService.update(vacancyId, vacancyDto);
    }

    @Operation(
            summary = "Filter vacancies by filter",
            description = """
                    Get vacancies filtered by given parameters.
                    Filter by name and position
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vacancies matching the given parameters",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = VacancyDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid filter parameters",
                            content = @Content(
                                    schema = @Schema(
                                            example = "{\"error\":"
                                                    + " \"Vacancy not found\"}")
                            )
                    )
            }
    )
    @PostMapping("/filter")
    List<VacancyDto> filterVacancies(
            @RequestBody
            @Parameter(
                    description = "Filter parameters for searching vacancies",
                    required = true
            )
            SearchVacancyDto searchVacancyDto) {
        return vacancyService.filterVacancies(searchVacancyDto);
    }

    @Operation(
            summary = "Get vacancy by Id",
            description = "Get all data about a vacancy by it's Id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vacancy found",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = VacancyDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Vacancy not found",
                            content = @Content(
                                    schema = @Schema(
                                            example = "{\"error\":"
                                                    + " \"Vacancy not found\"}")
                            )
                    )
            }
    )
    @GetMapping("/{vacancyId}")
    VacancyDto getVacancyById(
            @PathVariable
            @Parameter(description = "Vacancy Id", example = "1")
            long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

}
