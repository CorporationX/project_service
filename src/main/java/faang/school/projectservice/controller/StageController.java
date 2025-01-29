package faang.school.projectservice.controller;


import faang.school.projectservice.dto.client.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/stages")
@RequiredArgsConstructor
public class StageController {

    @GetMapping()
    @Operation(
            summary = "Создание этапа",
            description = "Этап относится к какому-то проекту. " +
                    "Все этапы хранятся в базе данных," +
                    " названия этапов в БД могут повторяться",
            tags = {"Creating Stage"}
    )
    public String creatingStage() {
        return "Creating Stage";
    }

    @GetMapping("/all")
    @Operation(
            summary = "Получить все этапы проекта.",
            description = "Получить все этапы проекта с фильтром по ролям " +
                    "(OWNER, MANAGER и др) и статусу задач " +
                    "(есть задачи в статусе In progress, все задачи в статусе done и др).",
            tags = {"Get all the stages of the project"})
    public String getAllStagesFilterByRoles() {
        return "all-projects";
    }

    @DeleteMapping("/{id}/delete")
    @Operation(
            summary = "Удалить этап.",
            description = "Когда этап удаляется, связанные с ним задачи можно: " +
                    "удалить каскадно, закрыть, перенести все задачи в другой этап.",
            tags = {"Delete stage"})
    public String deleteStage(@Parameter(description = "ID of the stage to delete",
            example = "123") @PathVariable("id") @NotNull Long id) {
        return String.format("Get project id: {}", id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stages found"),
            @ApiResponse(responseCode = "404", description = "Stages not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PatchMapping("/{id}/update")
    @Operation(
            summary = "Обновить этап.",
            description = "Если на этап требуется участник с определённой ролью, " +
                    "нужно проверить, что в списке задействованных на этапе участников " +
                    "есть пользователь с такой ролью. Если нет, то нужно найти среди " +
                    "участников проекта пользователя с такой ролью и отправить ему " +
                    "приглашение участвовать в этапе. Сколько пользователей с данной ролью требуется, " +
                    "столько приглашений разным пользователям должно быть отправлено. " +
                    "Если изменяется список участников, нужно проверять, что обновлённые " +
                    "список участников удовлетворяет требованиям ролей.",
            tags = {"Update project"})
    public String updateProject(@Parameter(description = "ID of the stage to update",
            example = "123") @PathVariable("id") @NotNull Long id) {
        return String.format("Update project id: {}", id);
    }

    @GetMapping("/users")
    @Operation(
            summary = "Получить всех пользователей",
            description = "Получить всех пользователей этого проекта.",
            tags = {"Get AllUsers"})
    public UserDto getAllUsers() {
        return new UserDto(1L, "Alice", "alice@email.com");
    }
}
