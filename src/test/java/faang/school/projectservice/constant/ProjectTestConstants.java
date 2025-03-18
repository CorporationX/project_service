package faang.school.projectservice.constant;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

public class ProjectTestConstants {
    public static final long OWNER_ID = 1L;
    public static final long NOT_OWNER_ID = 2L;

    public static final String PROJECT_COVER_IMAGE_ID = "projects_covers/12345-PROJECT_COVER_IMAGE.jpg";

    public static final long TEST_PROJECT_ID = 100L;

    public static final Project TEST_PROJECT = Project.builder()
            .id(TEST_PROJECT_ID)
            .name("Test")
            .description("Test project")
            .ownerId(OWNER_ID)
            .visibility(ProjectVisibility.PUBLIC)
            .status(ProjectStatus.CREATED)
            .build();
}
