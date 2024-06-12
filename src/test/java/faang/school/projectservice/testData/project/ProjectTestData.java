package faang.school.projectservice.testData.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

@Data
public class ProjectTestData {
    private Project project;
    private Project subproject;
    private ProjectDto projectDto;
    private ProjectDto subprojectDto;

    public ProjectTestData() {
        setProject();
        setSubproject();
    }


    private void setProject() {
        project = Project.builder()
                .id(1L)
                .name("Project")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
    }

    private void setSubproject() {
        subproject = Project.builder()
                .name("Subproject")
                .parentProject(project)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        subprojectDto = ProjectDto.builder()
                .name("Subproject")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .parentProjectId(project.getId())
                .build();
    }
}
