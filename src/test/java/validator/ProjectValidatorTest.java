package validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserServiceClient userServiceClient;

    private static final String PROJECT_NAME = "Name";
    private static final long CREATOR_ID = 1L;

    @Test
    public void shouldCheckIfUserProjectName_ProjectExists() {
        when(projectRepository.existsByOwnerIdAndName(CREATOR_ID, PROJECT_NAME)).thenReturn(true);
        Assertions.assertThrows(DataValidationException.class,
                () -> projectValidator.checkIfUserProjectName(PROJECT_NAME, CREATOR_ID));
    }

    @Test
    public void validateUserExists_ShouldNotThrow_WhenUserExists() {
        when(userServiceClient.checkUserExists(CREATOR_ID)).thenReturn(ResponseEntity.ok().build());

        Assertions.assertDoesNotThrow(() -> projectValidator.validateUserExists(CREATOR_ID));
    }

    @Test
    public void validateUserExists_ShouldThrow_WhenUserNotFound() {

        Request request = Request.create(
                Request.HttpMethod.GET,
                "/user/1/exists",
                Collections.emptyMap(),
                new byte[0], StandardCharsets.UTF_8,
                null
        );

        when(userServiceClient.checkUserExists(CREATOR_ID))
                .thenThrow(new FeignException.NotFound("User Not Found", request, null, null));

        Assertions.assertThrows(DataValidationException.class, () -> projectValidator.validateUserExists(CREATOR_ID));
    }
}