package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.moment.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
@Tag(name="Moments", description = "API для управления моментами. Моментами могут быть значимые для проекта даты, события, достижения")
@RestController
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @Operation(
            summary = "Создать момент",
            tags = { "moments", "post" })
    @PostMapping("/moments")
    public MomentDto createMoment(@RequestBody MomentDto momentDto) {
        momentValidate(momentDto);
        return momentService.create(momentDto);
    }

    @Operation(
            summary = "Обновить момент",
            tags = { "moments", "put" })
    @PutMapping("/moments/{id}")
    public MomentDto updateMoment(@PathVariable Long id,
                                  @RequestBody MomentDto momentDto) {
        momentValidate(momentDto);
        return momentService.update(id, momentDto);
    }

    @Operation(
            summary = "Получить все моменты",
            tags = { "moments", "get" })
    @GetMapping("/moments")
    public Page<MomentDto> getAllMoments(@RequestParam(value = "page") int page,
                                         @RequestParam(value = "pageSize") int pageSize) {
        return momentService.getAllMoments(page, pageSize);
    }

    @Operation(
            summary = "Получить моменты с применением фильтров",
            tags = { "moments", "post" })
    @PostMapping("/moments/byFilter")
    public Page<MomentDto> getAllMoments(@RequestBody MomentFilterDto filter,
                                         @RequestParam(value = "page") int page,
                                         @RequestParam(value = "pageSize") int pageSize) {
        return momentService.getAllMoments(page, pageSize, filter);
    }

    @Operation(
            summary = "Получить момент по ID",
            tags = { "moments", "put" })
    @GetMapping("/moments/{id}")
    public MomentDto getMomentById(@PathVariable Long id) {
        return momentService.getById(id);
    }

    private void momentValidate(MomentDto momentDto) {
        if (momentDto.getName() == null || momentDto.getName().isEmpty()) {
            throw new DataValidationException("moment name can not be empty");
        }
    }

}
