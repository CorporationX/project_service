package faang.school.projectservice.api;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@RequestMapping(path = "/vacancies")
@Tag(name = "Vacancies", description = "The vacancy API")
public interface VacancyApi {

    @GetMapping("/{id}")
    @Operation(summary = "Возвращает вакансию", description = "", tags = {"Vacancies"})
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = VacancyDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = DataValidationException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "405", description = "method not allowed (Не разрешён)",
            content = @Content(schema = @Schema(implementation = RuntimeException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    VacancyDto getVacancy(@PathVariable("id") Long id);

    @PostMapping("/get-all")
    @Operation(summary = "Возвращает все вакансии по фильтру", description = "",
            tags = {"Vacancies"})
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = VacancyDto.class)
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = DataValidationException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "405", description = "method not allowed (Не разрешён)",
            content = @Content(schema = @Schema(implementation = RuntimeException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    List<VacancyDto> getVacancies(@RequestBody VacancyFilterDto filter);

    @PostMapping("/create")
    @Operation(summary = "Создает и возвращает вакансию",
            description = "Возвращает созданную вакансию после сохранения в базу",
            tags = {"Vacancies"})
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = VacancyDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = DataValidationException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "405", description = "method not allowed (Не разрешён)",
            content = @Content(schema = @Schema(implementation = RuntimeException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    VacancyDto create(@RequestBody VacancyDto vacancyDto);

    @PutMapping("/{vacancyId}")
    @Operation(summary = "Обновляет и возвращает вакансию",
            description = "Возвращает обновлённую вакансию после сохранения в базу, " +
                    "так же можно закрыть вакансию передав статус",
            tags = {"Vacancies"})
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = VacancyDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = DataValidationException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "405", description = "method not allowed (Не разрешён)",
            content = @Content(schema = @Schema(implementation = RuntimeException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    VacancyDto update(@RequestBody VacancyDto updateDto);

    @PutMapping("/close/{id}")
    @Operation(summary = "Закрывает вакансию и возвращает дто со статусом CLOSE",
            description = "Возвращает закрываемую вакансию для проверки",
            tags = {"Vacancies"})
    @ApiResponse(responseCode = "200", description = "success (Успешно)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = VacancyDto.class)))
    @ApiResponse(responseCode = "400", description = "bad request (Неправильный запрос)",
            content = @Content(schema = @Schema(implementation = DataValidationException.class)))
    @ApiResponse(responseCode = "404", description = "not found (Не найден)",
            content = @Content(schema = @Schema(implementation = EntityNotFoundException.class)))
    @ApiResponse(responseCode = "405", description = "method not allowed (Не разрешён)",
            content = @Content(schema = @Schema(implementation = RuntimeException.class)))
    @ApiResponse(responseCode = "500", description = "server error (Ошибка сервера)",
            content = @Content(schema = @Schema(implementation = Void.class)))
    VacancyDto close(@PathVariable Long id);
}
