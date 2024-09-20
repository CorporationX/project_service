package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/moment")
@RequiredArgsConstructor
@Tag(name="Контроллер моментов", description="Позволяет работать с моментами")
public class MomentController {
    private final MomentService service;

    @PostMapping("/moment")
    @Operation(
            summary = "Создать момент",
            description = "Позволяет создать момент"
    )
    public MomentDto create(MomentDto momentDto) {
        validateMomentDto(momentDto);

        return service.create(momentDto);
    }

    @PutMapping("/update")
    @Operation(
            summary = "Обновить момент",
            description = "Позволяет обновить момент"
    )
    public MomentDto update(MomentDto momentDto) {
        validateMomentDto(momentDto);

        return service.update(momentDto);
    }

    @GetMapping("/moments")
    @Operation(
            summary = "Получить моменты по фильтру",
            description = "Позволяет получить моменты по фильтру"
    )
    public List<MomentDto> getMoments(MomentFilterDto filterDto) {
        return service.getMoments(filterDto);
    }

    @GetMapping("/get/all")
    @Operation(
            summary = "Получить все моменты",
            description = "Позволяет получить все моменты, которые есть в бд"
    )
    public List<MomentDto> getAllMoments() {
        return service.getAllMoments();
    }

    @PutMapping("/get/id")
    @Operation(
            summary = "Получить момент по id",
            description = "Позволяет получить момент по id"
    )
    public MomentDto getMoment(long id) {
        if (id < 1) {
            throw new DataValidationException("Передан некорректный id");
        }

        return service.getMoment(id);
    }

    private void validateMomentDto(MomentDto momentDto) {
        if (momentDto.name() == null || momentDto.name().isBlank()) {
            throw new DataValidationException("Наименование момента не может быть пустым");
        }
        if (momentDto.projectIds() == null || momentDto.projectIds().isEmpty()) {
            throw new DataValidationException("Момент должен относиться к какому-нибудь проекту");
        }
    }
}
