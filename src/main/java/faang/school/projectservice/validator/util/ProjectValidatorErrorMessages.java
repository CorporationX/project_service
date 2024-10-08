package faang.school.projectservice.validator.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProjectValidatorErrorMessages {
    public static final String COVER_IMAGE_FORMAT_ORIENTATION = "The project cover image should be horizontal or square";
    public static final String COVER_IMAGE_FILE_SIZE = "File for project cover image size should be less than: %s bytes";
    public static final String COVER_IMAGE_FORMAT = "Project cover image format should be: %s";
    public static final String NOT_OWNER_OF_PROJECT = "User with id: %s not owner of post with id: %s";
    public static final String PROJECT_RESOURCE_FILLED = "Resources of project with id: %s filled, free space: %s byte";
    public static final String PROJECT_HAS_NO_COVER = "Project with id: %s has no cover image";
}
