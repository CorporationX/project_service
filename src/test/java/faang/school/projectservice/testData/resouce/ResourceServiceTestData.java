package faang.school.projectservice.testData.resouce;

import faang.school.projectservice.model.*;
import lombok.Data;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
public class ResourceServiceTestData {
    private TeamMember teamMember;
    private Project project;
    private Resource resource;
    private MultipartFile file = new MockMultipartFile("name", "original_name", "TEXT", new byte[10000]);

    public ResourceServiceTestData() {
        setProject();
        setTeamMember();
        setResource();
    }


    private void setProject() {
        Project.builder()
                .id(1L)
                .name("Project")
                .storageSize(BigInteger.valueOf(10000))
                .maxStorageSize(BigInteger.valueOf(100000))
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
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
    }

    private void setTeamMember() {
        TeamMember.builder()
                .id(1L)
                .userId(1L)
                .team(Team.builder().project(project).build())
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
    }

    private void setResource() {
        Resource.builder()
                .id(1L)
                .key("TestKeyName")
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(new HashSet<>(teamMember.getRoles()))
                .type(ResourceType.valueOf(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(project)
                .build();
    }
}
