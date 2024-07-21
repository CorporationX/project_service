package faang.school.projectservice;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.ProjectStatusFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusFilterTest {

    private final ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
    private final StageFilterDto filterDto = new StageFilterDto();
    private List<Stage> stages = new ArrayList<>();

    @BeforeEach
    public void prepareStages() {
        Project firstProject = new Project();
        firstProject.setStatus(ProjectStatus.IN_PROGRESS);
        Project secondProject = new Project();
        secondProject.setStatus(ProjectStatus.CANCELLED);
        Stage firstStage = new Stage();
        firstStage.setStageId(1L);
        firstStage.setProject(firstProject);
        Stage secondStage = new Stage();
        secondStage.setStageId(2L);
        secondStage.setProject(secondProject);
        Stage thirdStage = new Stage();
        thirdStage.setProject(firstProject);
        thirdStage.setStageId(3L);
        stages = List.of(firstStage, secondStage, thirdStage);
    }

    @Test
    public void testProjectStatusFilterIsApplicable() {
        filterDto.setProjectStatus(ProjectStatus.IN_PROGRESS);

        boolean isApplicable = projectStatusFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testProjectStatusDoesNotIsApplicable() {

        boolean isApplicable = projectStatusFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testProjectStatusFilterApplied() {
        filterDto.setProjectStatus(ProjectStatus.IN_PROGRESS);

        Project testProject = new Project();
        testProject.setStatus(ProjectStatus.IN_PROGRESS);
        Stage firstTestStage = new Stage();
        firstTestStage.setStageId(1L);
        firstTestStage.setProject(testProject);
        Stage secondTestStage = new Stage();
        secondTestStage.setStageId(3L);
        secondTestStage.setProject(testProject);
        List<Stage> testResultStages = List.of(firstTestStage, secondTestStage);

        Stream<Stage> filterResult = projectStatusFilter.apply(stages.stream(), filterDto);
        assertEquals(filterResult.toList(), testResultStages);
    }
}

