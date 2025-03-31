package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.service.MeetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/meets")
@Tag(name = "Meet Management", description = "API для управления встречами")
public class MeetController {

    private final MeetService meetService;

    @Operation(
            summary = "Создать новую встречу",
            description = "Создает новую встречу с указанными параметрами",
            security = @SecurityRequirement(name = "user-id")
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetResponseDto createMeet(@RequestBody @Valid CreateMeetDto createMeetDto) {
        return meetService.createMeet(createMeetDto);
    }

    @Operation(summary = "Получить все встречи")
    @GetMapping
    public List<MeetResponseDto> findAll() {
        return meetService.findAll();
    }

    @Operation(summary = "Получить встречи проекта с фильтрацией")
    @GetMapping("/project/{projectId}")
    public List<MeetResponseDto> findProjectMeetsByFilter(
            @Parameter(description = "ID проекта") @PathVariable long projectId,
            @Parameter(description = "Параметры фильтрации") @Valid MeetFilterDto filter) {
        return meetService.findProjectMeetsByFilter(projectId, filter);
    }

    @Operation(summary = "Получить встречу по ID")
    @GetMapping("/{id}")
    public MeetResponseDto findById(
            @Parameter(description = "ID встречи") @PathVariable long id) {
        return meetService.findById(id);
    }

    @Operation(
            summary = "Обновить встречу",
            security = @SecurityRequirement(name = "user-id")
    )
    @PutMapping
    @ResponseStatus(OK)
    public MeetResponseDto updateMeet(@RequestBody @Valid UpdateMeetDto updateMeetDto) {
        return meetService.updateMeet(updateMeetDto);
    }

    @Operation(
            summary = "Отменить встречу",
            security = @SecurityRequirement(name = "user-id")
    )
    @PutMapping("/{id}/cancel")
    @ResponseStatus(OK)
    public MeetResponseDto cancelMeet(
            @Parameter(description = "ID встречи") @PathVariable long id) {
        return meetService.cancelMeet(id);
    }

    @Operation(
            summary = "Удалить встречу",
            security = @SecurityRequirement(name = "user-id")
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteMeet(
            @Parameter(description = "ID встречи") @PathVariable long id) {
        meetService.deleteMeet(id);
    }
}