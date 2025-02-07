package faang.school.projectservice.controller.stage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.service.stage.StageService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

//     Создание этапа. Этап ОБЯЗАТЕЛЬНО относится к какому-то проекту.
//     Все этапы хранятся в базе данных, названия этапов в БД могут повторяться.
//     При создании этапа необходимо определить список ролей и количество человек для каждой роли,
//     которые гарантированно будут задействованы на этапе.

    @PostMapping("/create")
    public StageDto createStage(@RequestBody @Valid StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

//    Получить все этапы проекта с фильтром по ролям (OWNER, MANAGER и др)
//     и статусу задач (есть задачи в статусе In progress, все задачи в статусе done и др).

    @GetMapping("/filter/{userId}")
    public List<StageDto> getAllStagesByRole(StageFilter filter, @PathVariable Long userId) {
        return stageService.getAllStagesByRole(filter, userId);
    }

    //Удалить этап.
    // Когда этап удаляется, связанные с ним задачи можно:
    // удалить каскадно, закрыть, перенести все задачи в другой этап.

    @DeleteMapping("/delete")
    public StageDto deleteStage() {
        return stageService.deleteStage();
    }

    //Обновить этап. Если на этап требуется участник с определённой ролью, нужно проверить,
    // что в списке задействованных на этапе участников есть пользователь с такой ролью.
    // Если нет, то нужно найти среди участников проекта пользователя с такой ролью
    // и отправить ему приглашение участвовать в этапе. Сколько пользователей с данной ролью требуется,
    // столько приглашений разным пользователям должно быть отправлено. Если изменяется список участников,
    // нужно проверять, что обновлённые список участников удовлетворяет требованиям ролей.

    @PutMapping("/update")
    public void updateStage() {

    }

    //Получить все этапы проекта.
    @GetMapping("projects/{projectId}/stages")
    public void getAllStages() {

    }

    //Получить конкретный этап по id.
    @GetMapping("/get-by/{id}")
    public StageDto getStageById(@PathVariable Long id) {
        return stageService.getStageById(id);
    }

}
