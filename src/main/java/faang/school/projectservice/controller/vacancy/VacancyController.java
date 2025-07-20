package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления вакансиями.
 * <p>
 * Предоставляет endpoints для создания, обновления, получения списка и получения вакансии по идентификатору.
 *
 * @author Myrza
 * @since 20.07.2025
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/vacancies")
@Slf4j
public class VacancyController {
    private final VacancyService service;

    /**
     * Создание новой вакансии.
     *
     * @param createDto DTO с данными для создания вакансии.
     * @return Ответ с созданной вакансией {@link VacancyDto}.
     */
    @PostMapping
    public ResponseEntity<VacancyDto> create(@Valid @RequestBody VacancyCreateDto createDto) {
        log.info("create vacancy");
        var vacancy = service.create(createDto);
        return ResponseEntity.ok(vacancy);
    }

    /**
     * Обновление существующей вакансии по её идентификатору.
     *
     * @param vacancyId Идентификатор вакансии.
     * @param updateDto DTO с обновлёнными данными.
     * @return Ответ с обновлённой вакансией {@link VacancyDto}.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VacancyDto> update(@PathVariable Long vacancyId,
                                             @Valid @RequestBody VacancyUpdateDto updateDto) {
        var vacancy = service.update(vacancyId, updateDto);
        return ResponseEntity.ok(vacancy);
    }

    /**
     * Получение списка вакансий по заданным фильтрам.
     *
     * @param filterDto DTO с параметрами фильтрации.
     * @return Список вакансий {@link VacancyDto}, удовлетворяющих условиям фильтра.
     */
    @GetMapping
    public ResponseEntity<List<VacancyDto>> getList(@Valid @ModelAttribute VacancyFilterDto filterDto) {
        var vacancies = service.getList(filterDto);
        return ResponseEntity.ok(vacancies);
    }

    /**
     * Получение вакансии по её идентификатору.
     *
     * @param vacancyId Идентификатор вакансии.
     * @return DTO с информацией о вакансии {@link VacancyDto}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VacancyDto> getById(@PathVariable Long vacancyId) {
        var vacancy = service.getById(vacancyId);
        return ResponseEntity.ok(vacancy);
    }
}
