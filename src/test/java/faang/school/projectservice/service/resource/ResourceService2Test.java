package faang.school.projectservice.service.resource;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceService2Test {

    private static final String RESOURCE_KEY = "123";
    private static final String TEST_FILE_NAME = "testFile";
    private static final long TEST_FILE_SIZE = 5000L;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ResourceService2 resourceService2;

    @Nested
    @DisplayName("Retrieving a resource by KEY")
    class GetResourceById {

        @Test
        @DisplayName("should return the resource when it is found")
        void whenResourceIsFoundThenReturnResource() {
            Resource resource = new Resource();
            resource.setKey(RESOURCE_KEY);
            when(resourceRepository.findByKey(RESOURCE_KEY)).thenReturn(Optional.of(resource));

            Resource result = resourceService2.getResourceByKey(RESOURCE_KEY);

            assertNotNull(result);
            assertEquals(RESOURCE_KEY, result.getKey());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when resource is not found")
        void whenResourceIsNotFoundThenThrowEntityNotFoundException() {
            when(resourceRepository.findByKey(RESOURCE_KEY)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                resourceService2.getResourceByKey(RESOURCE_KEY);
            });

            assertEquals("Resource with key 123 doesn't exist", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Adding a new resource")
    class PutResource {

        @Test
        @DisplayName("should save and return the resource")
        void whenNewResourceIsAddedThenSaveAndReturnResource() {
            Resource resource = new Resource();
            resource.setKey(RESOURCE_KEY);

            when(multipartFile.getName()).thenReturn(TEST_FILE_NAME);
            when(multipartFile.getSize()).thenReturn(TEST_FILE_SIZE);
            when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

            Resource result = resourceService2.putResource(RESOURCE_KEY, multipartFile, ResourceType.IMAGE);

            assertNotNull(result);
            assertEquals(RESOURCE_KEY, result.getKey());
            verify(resourceRepository).save(any(Resource.class));
        }
    }

    @Nested
    @DisplayName("Marking a resource as deleted")
    class MarkResourceAsDeleted {

        @Test
        @DisplayName("should mark the resource as deleted and return it")
        void whenResourceIsDeletedThenReturnUpdatedResource() {
            Resource resource = new Resource();
            resource.setKey(RESOURCE_KEY);
            resource.setStatus(ResourceStatus.ACTIVE);

            when(resourceRepository.findByKey(RESOURCE_KEY)).thenReturn(Optional.of(resource));
            when(resourceRepository.save(resource)).thenReturn(resource);

            Resource result = resourceService2.markResourceAsDeleted(RESOURCE_KEY);

            assertNotNull(result);
            assertEquals(ResourceStatus.DELETED, result.getStatus());
            verify(resourceRepository).save(resource);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when resource is not found")
        void whenResourceIsNotFoundThenThrowEntityNotFoundException() {
            when(resourceRepository.findByKey(RESOURCE_KEY)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                resourceService2.markResourceAsDeleted(RESOURCE_KEY);
            });

            assertEquals("Resource with key 123 doesn't exist", exception.getMessage());
        }
    }
}