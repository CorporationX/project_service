package faang.school.projectservice.service.resource;

import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.s3.requests.S3RequestService;
import faang.school.projectservice.testData.resouce.ResourceServiceTestData;
import faang.school.projectservice.validator.resource.TeamMemberResourceValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private S3Service s3Service;
    @Mock
    private S3RequestService s3RequestService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private TeamMemberResourceValidator teamMemberResourceValidator;
    @InjectMocks
    private ResourceService resourceService;

    private TeamMember teamMember;
    private Project project;
    private Resource resource;

    @BeforeEach
    void init() {
        ResourceServiceTestData resourceServiceTestData = new ResourceServiceTestData();
        project = resourceServiceTestData.getProject();
        teamMember = resourceServiceTestData.getTeamMember();
        resource = resourceServiceTestData.getResource();

        when(teamMemberRepository.findById(teamMember.getId())).thenReturn(teamMember);
        when(resourceRepository.getReferenceById(resource.getId())).thenReturn(resource);
    }

    @Nested
    class PositiveTests {
        //TODO Murzin34* Тесты на получение ресурса.
        @Test
        public void testGetResourcesSuccess() {
            resourceService.getResources(teamMember.getId(), resource.getId());

            verify(teamMemberRepository).findById(teamMember.getId());
            verify(resourceRepository).getReferenceById(resource.getId());
            verify(teamMemberResourceValidator).validateDownloadFilePermission(teamMember, resource);
            verify(s3Service).getFile(any());
        }

        @Test
        void testGetResourcesFillSuccess() {
            InputStreamResource expected = new InputStreamResource(InputStream.nullInputStream());
            when(s3Service.getFile(s3RequestService.createGetRequest(resource.getKey()))).thenReturn(expected);

            InputStreamResource actual = resourceService.getResources(resource.getId(), teamMember.getId());

            assertNotNull(expected);
            Assertions.assertEquals(expected, actual);

            verify(teamMemberResourceValidator).validateDownloadFilePermission(teamMember, resource);
        }

        @Test
        void testDeleteResourceSuccess() {
            var expectedResources = project.getResources().remove(resource);
            var expectedLength = project.getStorageSize().subtract(resource.getSize());
            var deleteRequest = s3RequestService.createDeleteRequest(resource.getKey());

            resourceService.deleteResource(teamMember.getId(), resource.getId());

            assertEquals(expectedResources, project.getResources().contains(resource));
            assertEquals(expectedLength, project.getStorageSize());

            assertNull(resource.getKey());
            assertNull(resource.getSize());
            assertNull(resource.getAllowedRoles());
            assertEquals(ResourceStatus.DELETED, resource.getStatus());
            assertEquals(teamMember, resource.getUpdatedBy());

            verify(resourceRepository).save(resource);
            verify(projectRepository).save(project);
            verify(s3Service).deleteFile(deleteRequest);
            verify(teamMemberResourceValidator).validateDeleteFilePermission(teamMember, resource);
        }
    }

    @Nested
    class NegativeTests {
        //TODO Murzin34
    }
}