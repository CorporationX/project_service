package faang.school.projectservice.util;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.List;

public class TestProject {
    public static long PROJECT_ID_1 = 1L;
    public static long PROJECT_ID_2 = 2L;
    public static long PROJECT_ID_3 = 3L;
    public static long PROJECT_ID_4 = 4L;
    public static long OWNER_ID = 1L;
    public static String PROJECT_NAME = "Project";
    public static String PROJECT_DESC = "Desc";

    public static Project PROJECT = Project.builder()
            .id(PROJECT_ID_1)
            .name(PROJECT_NAME)
            .description(PROJECT_DESC)
            .ownerId(OWNER_ID)
            .status(ProjectStatus.CREATED)
            .visibility(ProjectVisibility.PRIVATE)
            .build();

    public static ProjectDto PROJECT_DTO = ProjectDto.builder()
            .id(PROJECT_ID_1)
            .name(PROJECT_NAME)
            .description(PROJECT_DESC)
            .status(ProjectStatus.CREATED)
            .ownerId(OWNER_ID)
            .visibility(ProjectVisibility.PRIVATE)
            .build();

    public static Project SAVED_PROJECT = Project.builder()
            .id(PROJECT_ID_1)
            .name(PROJECT_NAME)
            .description(PROJECT_DESC)
            .ownerId(OWNER_ID)
            .status(ProjectStatus.CREATED)
            .build();

    public static Project CANCELLED_PROJECT = Project.builder()
            .id(PROJECT_ID_1)
            .status(ProjectStatus.CANCELLED)
            .build();

    public static Project COMPLETED_PROJECT = Project.builder()
            .id(PROJECT_ID_1)
            .status(ProjectStatus.COMPLETED)
            .build();

    public static List<Project> PROJECTS = List.of(PROJECT);
    public static List<ProjectDto> PROJECTS_DTOS = List.of(PROJECT_DTO);

    private static final Project project_first = Project.builder()
            .name("Project first")
            .build();
    private static final Project project_second = Project.builder()
            .name("Project second")
            .build();

    public static List<Project> PROJECTS_LIST = List.of(project_first, project_second);
    public static List<Project> PROJECT_FILTERED = List.of(project_first);

    public static List<Long> EXPECTED_PROJECT_IDS = List.of(PROJECT_ID_1, PROJECT_ID_2,
            PROJECT_ID_3, PROJECT_ID_4);

    public static List<Long> PROJECTS_ID = List.of(PROJECT_ID_1, PROJECT_ID_2);

    public static List<Project> COMPLETED_PROJECTS = List.of(COMPLETED_PROJECT);

    public static List<Project> CANCELLED_PROJECTS = List.of(CANCELLED_PROJECT);
}
