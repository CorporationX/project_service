package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.filter.StageTaskFilter;
import faang.school.projectservice.filter.TaskFilterEnum;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class StageTaskFilterTest {
    private Stream<Stage> stageStream;
    private StageFilterDto filterDto = new StageFilterDto();
    StageTaskFilter taskFilter;

    @BeforeEach
    void init() {
        taskFilter = new StageTaskFilter();
    }


    public List<Stage> initStages() {//возвращает массив Stage, чтобы можно было что то с ними сделать
        Stage stage1 = new Stage();
        Stage stage2 = new Stage();
        Stage stage3 = new Stage();

        Task task1 = new Task();
        Task task2 = new Task();
        Task task22 = new Task();
        Task task3 = new Task();
        Task task33 = new Task();

        task1.setStatus(TaskStatus.TODO);
        task2.setStatus(TaskStatus.TESTING);
        task22.setStatus(TaskStatus.TESTING);
        task3.setStatus(TaskStatus.CANCELLED);
        task33.setStatus(TaskStatus.CANCELLED);

        stage1.setTasks(List.of(task1, task2));
        stage2.setTasks(List.of(task22, task3));
        stage3.setTasks(List.of(task33, task33));

        stageStream = Stream.of(stage1, stage2, stage3);

        return List.of(stage1, stage2, stage3);
    }

    @Test
    void apply_whenOk_any() {
        List<Stage> stages = new ArrayList<>(initStages());

        filterDto.setRoleFilterEnum(TaskFilterEnum.ANY);
        filterDto.setStatus(TaskStatus.TESTING);

        Stream<Stage> stageStream1 = taskFilter.apply(stageStream, filterDto);

        stages.remove(2);
        Assertions.assertEquals(stageStream1.toList(), stages);
    }

    @Test
    void apply_whenOk_all() {
        List<Stage> stages = new ArrayList<>(initStages());

        filterDto.setRoleFilterEnum(TaskFilterEnum.ALL);
        filterDto.setStatus(TaskStatus.CANCELLED);

        Stream<Stage> stageStream1 = taskFilter.apply(stageStream, filterDto);

        stages.remove(0);
        stages.remove(0);
        Assertions.assertEquals(stageStream1.toList(), stages);
    }

    @Test
    void isApplicable_whenOk() {
        filterDto.setStatus(null);
        Assertions.assertFalse(taskFilter.isApplicable(filterDto));

        filterDto.setStatus(TaskStatus.TESTING);
        filterDto.setRoleFilterEnum(TaskFilterEnum.ANY);
        Assertions.assertTrue(taskFilter.isApplicable(filterDto));
    }

}
