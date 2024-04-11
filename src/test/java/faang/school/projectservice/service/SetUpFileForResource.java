package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class SetUpFileForResource {
    protected long projectId = 1;
    protected long userId = 1;
    protected MockMultipartFile file;
    protected byte[] content;
    protected Project firstProject;
    protected Project secondProject;
    protected Resource firstResource;
    protected Resource thirdResource;
    protected TeamMember firstTeamMember;
    protected TeamMember secondTeamMember;
    protected String folder;

    @BeforeEach
    void setUp() {

        firstProject = Project.builder()
                .id(1L)
                .name("firstProject")
                .description("string")
                .storageSize(BigInteger.valueOf(121212))
                .maxStorageSize(BigInteger.valueOf(2000000000))
                .ownerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        secondProject = Project.builder()
                .id(1L)
                .name("firstProject")
                .description("string")
                .storageSize(BigInteger.valueOf(9212120000000L))
                .maxStorageSize(BigInteger.valueOf(2000000000))
                .ownerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        firstTeamMember = TeamMember.builder()
                .team(new Team())
                .id(2L)
                .userId(userId)
                .build();

        firstResource = Resource.builder()
                .id(1L)
                .name("resource")
                .key("1firstProject/resource")
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(firstProject)
                .size(BigInteger.valueOf(121212))
                .createdBy(firstTeamMember)
                .build();
        secondTeamMember = TeamMember.builder()
                .id(5L)
                .userId(5L)
                .team(new Team())
                .build();

        thirdResource = Resource.builder()
                .id(1L)
                .name("resource")
                .key("1firstProject/resource")
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(secondProject)
                .size(BigInteger.valueOf(121212))
                .createdBy(secondTeamMember)
                .build();

        content = "Mock file content".getBytes();
        file = new MockMultipartFile("file", "test.txt", "text/plain", content);

        folder = firstProject.getId() + firstProject.getName();

    }
}
