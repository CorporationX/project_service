package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectNameFilterTest {
    @InjectMocks
    private ProjectStatusFilter projectStatusFilter;
    List<Project> projects;
    @BeforeEach
    public void init(){
        projects = List.of(
                Project.builder()
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.ON_HOLD)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.CANCELLED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.COMPLETED)
                        .build()
        );
    }

    @Test
    void testReturnIsTrueIfFilterIsSpecified() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        boolean isApplicable = projectStatusFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }


    @Test
    void apply() {
    }
}