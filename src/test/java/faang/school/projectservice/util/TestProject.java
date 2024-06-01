package faang.school.projectservice.util;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public class TestProject {
    public static long PROJECT_ID = 1L;
    public static String PROJECT_NAME = "Project";
    public static String PROJECT_DESC = "Desc";

    public static Project PROJECT = Project.builder()
            .id(PROJECT_ID)
            .name(PROJECT_NAME)
            .description(PROJECT_DESC)
            .build();

    public static ProjectDto PROJECT_DTO = ProjectDto.builder()
            .id(PROJECT_ID)
            .name(PROJECT_NAME)
            .description(PROJECT_DESC)
            .build();

    public static List<Project> PROJECTS = List.of(PROJECT);
    public static List<ProjectDto> PROJECTS_DTOS = List.of(PROJECT_DTO);
}
