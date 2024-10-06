package faang.school.projectservice.validator;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.util.project.ProjectFabric.buildMultiPartFile;
import static faang.school.projectservice.util.project.ProjectFabric.buildProject;
import static faang.school.projectservice.util.project.ProjectFabric.buildProjectCoverImageId;
import static faang.school.projectservice.util.project.ProjectFabric.buildResource;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.NOT_OWNER_OF_PROJECT;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.PROJECT_HAS_NO_COVER;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.PROJECT_RESOURCE_FILLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectServiceValidatorTest {
    private static final long OWNER_ID = 1L;
    private static final long NOT_OWNER_ID = 2L;
    private static final long PROJECT_ID = 1L;
    private static final BigInteger MAX_STORAGE_SIZE = BigInteger.valueOf(5);
    private static final BigInteger STORAGE_SIZE = BigInteger.valueOf(1);
    private static final String COVER_ID = "cover";
    private static final String BLANK_COVER_ID = "";
    private static final long PROJECT_NEW_SIZE = 2;

    private final ProjectServiceValidator projectServiceValidator = new ProjectServiceValidator();

    @Test
    @DisplayName("Test validate project owner valid")
    void testProjectOwnerValid() {
        Project project = buildProject(PROJECT_ID, OWNER_ID);

        projectServiceValidator.projectOwner(project, OWNER_ID);
    }

    @Test
    @DisplayName("Test validate project owner un valid")
    void testProjectOwnerUnValid() {
        Project project = buildProject(PROJECT_ID, OWNER_ID);

        assertThatThrownBy(() -> projectServiceValidator.projectOwner(project, NOT_OWNER_ID))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(NOT_OWNER_OF_PROJECT, NOT_OWNER_ID, OWNER_ID);
    }

    @Test
    @DisplayName("Given resource key null when check then return empty")
    void testGetResourceByKeyResourceKeyNull() {
        Project project = buildProject(PROJECT_ID);

        assertThat(projectServiceValidator.getResourceByKey(project, null))
                .isEmpty();
    }

    @Test
    @DisplayName("Given project with resource list null when check then return empty")
    void testGetResourceByKeyProjectResourceNull() {
        Project project = buildProject(PROJECT_ID);

        assertThat(projectServiceValidator.getResourceByKey(project, COVER_ID))
                .isEmpty();
    }

    @Test
    @DisplayName("Get resource by key successful")
    void testGetResourceByKeySuccessful() {
        Resource resource = buildResource(COVER_ID);
        Project project = buildProject(PROJECT_ID, List.of(resource));

        assertThat(projectServiceValidator.getResourceByKey(project, COVER_ID))
                .isEqualTo(Optional.of(resource));
    }

    @Test
    @DisplayName("Given resource null when check then assigned zero")
    void testValidResourceSizeResourceIsNull() {
        Project project = buildProject(PROJECT_ID, STORAGE_SIZE, MAX_STORAGE_SIZE);
        MultipartFile multipartFile = buildMultiPartFile(PROJECT_NEW_SIZE);

        assertThat(projectServiceValidator.validResourceSize(project, multipartFile.getSize()))
                .isEqualTo(STORAGE_SIZE.longValue() + PROJECT_NEW_SIZE);
    }

    @Test
    @DisplayName("Given project with null storage size when check then assign zero")
    void testValidResourceSizeStorageSizeIsNull() {
        Project project = buildProject(PROJECT_ID, null, MAX_STORAGE_SIZE);
        MultipartFile multipartFile = buildMultiPartFile(PROJECT_NEW_SIZE);

        assertThat(projectServiceValidator.validResourceSize(project, multipartFile.getSize()))
                .isEqualTo(PROJECT_NEW_SIZE);
    }

    @Test
    @DisplayName("Given new file size more than max storage size when check then throw exception")
    void testValidResourceSizeResourceFilled() {
        Project project = buildProject(PROJECT_ID, MAX_STORAGE_SIZE, MAX_STORAGE_SIZE);
        MultipartFile multipartFile = buildMultiPartFile(PROJECT_NEW_SIZE);

        assertThatThrownBy(() -> projectServiceValidator.validResourceSize(project, multipartFile.getSize()))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(PROJECT_RESOURCE_FILLED, PROJECT_ID, 0);
    }

    @Test
    @DisplayName("Validate new resource size successful ")
    void testValidResourceSizeSuccessful() {
        Project project = buildProject(PROJECT_ID, STORAGE_SIZE, MAX_STORAGE_SIZE);
        MultipartFile multipartFile = buildMultiPartFile(PROJECT_NEW_SIZE);

        assertThat(projectServiceValidator.validResourceSize(project, multipartFile.getSize()))
                .isEqualTo(STORAGE_SIZE.longValue() + PROJECT_NEW_SIZE);
    }

    @Test
    @DisplayName("Given project with cover id null when check then throw exception")
    void testGetCoverImageIdImageIdIsNull() {
        Project project = buildProject(PROJECT_ID);

        assertThatThrownBy(() -> projectServiceValidator.coverImageId(project))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(PROJECT_HAS_NO_COVER, PROJECT_ID);
    }

    @Test
    @DisplayName("Given project with cover id is blank when check then throw exception")
    void testGetCoverImageIdImageIdIsBlank() {
        Project project = buildProjectCoverImageId(PROJECT_ID, BLANK_COVER_ID);

        assertThatThrownBy(() -> projectServiceValidator.coverImageId(project))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(PROJECT_HAS_NO_COVER, PROJECT_ID);
    }

    @Test
    @DisplayName("Get cover image id successful ")
    void testGetCoverImageIdSuccessful() {
        Project project = buildProjectCoverImageId(PROJECT_ID, COVER_ID);

        assertThat(projectServiceValidator.coverImageId(project))
                .isEqualTo(COVER_ID);
    }
}