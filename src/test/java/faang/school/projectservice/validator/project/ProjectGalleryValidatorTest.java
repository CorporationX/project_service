package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.AccessException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectGalleryValidatorTest {
    private static final int MAX_GALLERY_SIZE = 10;
    private static final int MAX_IMAGE_SIZE_MB = 5;

    private ProjectGalleryValidator validator;

    @Mock
    private ProjectRepository projectRepository;

    private Project project;
    private final Long userId = 1L;
    private final Long anotherUserId = 2L;

    @BeforeEach
    void setUp() {
        validator = new ProjectGalleryValidator(MAX_GALLERY_SIZE, MAX_IMAGE_SIZE_MB, projectRepository);

        project = new Project();
        project.setId(1L);
        project.setGalleryFileKeys(new ArrayList<>());
        project.setVisibility(ProjectVisibility.PRIVATE);
    }

    @Test
    void validateAddingImageTest_ShouldPassWhenValidImage() {
        MockMultipartFile file = new MockMultipartFile("image",
                "test.jpg",
                "image/jpeg",
                new byte[1024]);

        when(projectRepository.isUserMemberOfProject(project.getId(), userId)).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateAddingImage(project, userId, file));
    }

    @Test
    void validateAddingImageTest_ShouldThrowExceptionWhenGalleryFull() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < MAX_GALLERY_SIZE; i++) {
            strings.add("test " + i);
        }
        project.setGalleryFileKeys(strings);

        MockMultipartFile file = new MockMultipartFile("image",
                "test.jpg",
                "image/jpeg",
                new byte[1024]);

        when(projectRepository.isUserMemberOfProject(project.getId(), userId)).thenReturn(true);

        Exception exception = assertThrows(DataValidationException.class, () ->
                validator.validateAddingImage(project, userId, file));
        assertEquals("Gallery is full. Delete some images first.", exception.getMessage());
    }

    @Test
    void validateAddingImageTest_ShouldThrowExceptionWhenImageTooLarge() {
        MockMultipartFile file = new MockMultipartFile("image",
                "large.jpg",
                "image/jpeg",
                new byte[(MAX_IMAGE_SIZE_MB + 1) * 1024 * 1024]);

        when(projectRepository.isUserMemberOfProject(project.getId(), userId)).thenReturn(true);

        Exception exception = assertThrows(DataValidationException.class, () ->
                validator.validateAddingImage(project, userId, file));

        assertTrue(exception.getMessage().contains("Max image size is " + MAX_IMAGE_SIZE_MB + " mb."));
    }

    @Test
    void validateAddingImageTest_ShouldThrowExceptionWhenNotAnImage() {
        MockMultipartFile file = new MockMultipartFile("image",
                "text.txt",
                "text/plain", new byte[1024]);

        when(projectRepository.isUserMemberOfProject(project.getId(), userId)).thenReturn(true);

        Exception exception = assertThrows(DataValidationException.class, () ->
                validator.validateAddingImage(project, userId, file));

        assertEquals("File content type is not supported.", exception.getMessage());
    }

    @Test
    void validateDeletingImageTest_ShouldPassWhenUserIsMember() {
        when(projectRepository.isUserMemberOfProject(project.getId(), userId)).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateDeletingImage(project, userId));
    }

    @Test
    void validateDeletingImageTest_ShouldThrowExceptionWhenUserNotMember() {
        when(projectRepository.isUserMemberOfProject(project.getId(), userId)).thenReturn(false);

        Exception exception = assertThrows(AccessException.class, () ->
                validator.validateDeletingImage(project, userId));

        assertEquals("User " + userId + " is not a member of this project", exception.getMessage());
    }

    @Test
    void validateGettingGalleryTest_ShouldPassWhenProjectPublic() {
        project.setVisibility(ProjectVisibility.PUBLIC);

        assertDoesNotThrow(() -> validator.validateGettingGallery(project, anotherUserId));
    }

    @Test
    void validateGettingGalleryTest_ShouldPassWhenUserIsMemberOfPrivateProject() {
        when(projectRepository.isUserMemberOfProject(project.getId(), userId)).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateGettingGallery(project, userId));
    }

    @Test
    void validateGettingGalleryTest_ShouldThrowExceptionWhenUserNotMemberOfPrivateProject() {
        when(projectRepository.isUserMemberOfProject(project.getId(), anotherUserId)).thenReturn(false);

        Exception exception = assertThrows(AccessException.class, () ->
                validator.validateGettingGallery(project, anotherUserId));

        assertEquals("You do not have permission to access this project gallery.", exception.getMessage());
    }
}