package faang.school.projectservice.controller;

import faang.school.projectservice.dto.sub_project.SubProjectCreateDto;
import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.dto.sub_project.SubProjectUpdateDto;
import faang.school.projectservice.dto.sub_project.SubProjectViewDto;
import faang.school.projectservice.service.SubProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * REST-контроллера для управления подпроектами
 * Предоставляет API для операций CRUD с подпроектами:
 * <ul>
 *   <li>Создание новых подпроектов</li>
 *   <li>Обновление существующих подпроектов</li>
 *   <li>Получение отфильтрованных подпроектов</li>
 * </ul>
 * </p>
 *
 * @author Linempy
 * @since 21.07.2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/subprojects")
public class SubProjectController {

    private final SubProjectService service;

    @PostMapping()
    public SubProjectViewDto create(
            @Valid @RequestBody SubProjectCreateDto createDto) {
        return service.create(createDto);
    }

    @PutMapping("/{id}")
    public SubProjectViewDto update(@PathVariable Long id,
                                    @RequestBody SubProjectUpdateDto updateDto) {
        return service.update(id, updateDto);
    }

    @GetMapping("/{parentId}")
    public List<SubProjectViewDto> getByFilter(
            @PathVariable Long parentId,
            @ModelAttribute SubProjectFilterDto filterDto) {
        return service.getByFilter(parentId, filterDto);
    }
}