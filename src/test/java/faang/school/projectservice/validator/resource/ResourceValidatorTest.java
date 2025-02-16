package faang.school.projectservice.validator.resource;

import faang.school.projectservice.config.resource.ResourceConfig;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.validator.project.ResourceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {

    @Mock
    private MultipartFile file;

    @Mock
    private ResourceConfig resourceConfig;

    private final ResourceValidator resourceValidator = new ResourceValidator(resourceConfig);

    @Test
    void validateResourceThrowExceptionIfFileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);
        assertThrows(DataValidationException.class, () -> resourceValidator.validateResource(file));
    }

    @Test
    void validateResourceThrowExceptionIfFileIsNull() {
        assertThrows(DataValidationException.class, () -> resourceValidator.validateResource(null));
    }
}