package faang.school.projectservice.testData.resouce;

import faang.school.projectservice.model.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ResourceServiceTestData {
    private Project project;
    private Project anotherProject;
    private final MultipartFile file = new MockMultipartFile("name", "original_name", "TEXT", new byte[10000]);

    public Project getProject() {
        return project = Project.builder()
                .id(1L)
                .name("Project")
                .storageSize(BigInteger.valueOf(10000))
                .maxStorageSize(BigInteger.valueOf(100000))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .resources(new ArrayList<>(List.of(Resource.builder()
                        .id(1L)
                        .key("TestKeyName")
                        .name(file.getName())
                        .size(BigInteger.valueOf(file.getSize()))
                        .allowedRoles(new HashSet<>(getTeamMember().getRoles()))
                        .type(ResourceType.valueOf(file.getContentType()))
                        .status(ResourceStatus.ACTIVE)
                        .createdBy(getTeamMember())
                        .updatedBy(getTeamMember())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .project(project)
                        .build())))
                .build();
    }

    public TeamMember getTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .team(Team.builder().project(project).build())
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
    }

    public Resource getResource() {
        return Resource.builder()
                .id(1L)
                .key("TestKeyName")
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(new HashSet<>(getTeamMember().getRoles()))
                .type(ResourceType.valueOf(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(getTeamMember())
                .updatedBy(getTeamMember())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(project)
                .build();
    }

    public void getAnotherProject() {
        anotherProject = Project.builder()
                .id(2L)
                .name("Project2")
                .storageSize(BigInteger.valueOf(10000))
                .maxStorageSize(BigInteger.valueOf(100000))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .resources(new ArrayList<>(List.of(Resource.builder()
                        .id(2L)
                        .key("TestKeyName2")
                        .name(file.getName())
                        .size(BigInteger.valueOf(file.getSize()))
                        .allowedRoles(new HashSet<>(getTeamMemberFromAnotherProject().getRoles()))
                        .type(ResourceType.valueOf(file.getContentType()))
                        .status(ResourceStatus.ACTIVE)
                        .createdBy(getTeamMemberFromAnotherProject())
                        .updatedBy(getTeamMemberFromAnotherProject())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .project(anotherProject)
                        .build())))
                .build();
    }

    public TeamMember getTeamMemberFromAnotherProject() {
        return TeamMember.builder()
                .id(2L)
                .userId(2L)
                .team(Team.builder().project(anotherProject).build())
                .roles(new ArrayList<>(List.of(TeamRole.OWNER)))
                .build();
    }

    public TeamMember getTeamMemberFromSameProject() {
        return TeamMember.builder()
                .id(3L)
                .userId(3L)
                .team(Team.builder().project(project).build())
                .roles(new ArrayList<>(List.of()))
                .build();
    }
}
