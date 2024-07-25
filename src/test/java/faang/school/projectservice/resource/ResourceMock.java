package faang.school.projectservice.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.util.ProjectMock;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class ResourceMock {

    public static LocalDateTime now = LocalDateTime.now();

    public static long resourceId = 1;
    public static String resourceUrl = "http://localhost:8080/url-to-resource";
    public static String minioKey = "111";
    public static String resourceName = "avatar.jpg";
    public static BigInteger fileSize = BigInteger.valueOf(30000);

    public static long teamMemberId = 1;
    public static long userId = 1;
    public static long projectId = 1;

    public static TeamMember generateTeamMember(long id, long userId, List<TeamRole> roles) {
        return TeamMember.builder()
                .id(id)
                .userId(userId)
                .roles(roles)
                .build();
    }

    public static TeamMember generateTeamMember() {
        return ResourceMock.generateTeamMember(teamMemberId, userId, List.of(TeamRole.OWNER));
    }

    public static TeamMember generateTeamMember(long teamMemberId) {
        return ResourceMock.generateTeamMember(teamMemberId, userId, List.of(TeamRole.OWNER));
    }

    public static Resource generateResource(
            long resourceId,
            String resourceName,
            String minioKey,
            BigInteger fileSize,
            List<TeamRole> allowedRoles,
            ResourceType type,
            ResourceStatus status,
            TeamMember createdBy,
            TeamMember updatedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Project project
    ) {
        return Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .key(minioKey)
                .size(fileSize)
                .allowedRoles(allowedRoles)
                .type(type)
                .status(status)
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .project(project)
                .build();
    }

    public static Resource generateResource() {
        TeamMember teamMember = generateTeamMember();

        return ResourceMock.generateResource(
                resourceId,
                resourceName,
                minioKey,
                fileSize,
                List.of(TeamRole.OWNER),
                ResourceType.IMAGE,
                ResourceStatus.ACTIVE,
                teamMember,
                teamMember,
                now,
                now,
                ProjectMock.generateProject(projectId)
        );
    }

    public static Resource generateResource(ResourceStatus status) {
        TeamMember teamMember = generateTeamMember();

        return Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .key(minioKey)
                .size(fileSize)
                .allowedRoles(List.of(TeamRole.OWNER))
                .type(ResourceType.IMAGE)
                .status(status)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .createdAt(now)
                .updatedAt(now)
                .project(ProjectMock.generateProject(projectId))
                .build();
    }

    public static Resource generateResource(String resourceName, BigInteger fileSize) {
        TeamMember teamMember = generateTeamMember();

        return Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .key(minioKey)
                .size(fileSize)
                .allowedRoles(List.of(TeamRole.OWNER))
                .type(ResourceType.IMAGE)
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .createdAt(now)
                .updatedAt(now)
                .project(ProjectMock.generateProject(projectId))
                .build();
    }

    public static MockMultipartFile generateMultipartFile(String resourceName, BigInteger fileSize) {
        return new MockMultipartFile(
                "file",
                resourceName,
                "image/jpg",
                generateByteArray(fileSize.intValue())
        );
    }

    public static MockMultipartFile generateMultipartFile() {
        return ResourceMock.generateMultipartFile(resourceName, fileSize);
    }

    public static ResourceDto generateResourceDto(long resourceId, String resourceName, BigInteger fileSize, long projectId) {
        return new ResourceDto(
                resourceId,
                resourceName,
                fileSize,
                projectId
        );
    }

    public static ResourceDto generateResourceDto() {
        return ResourceMock.generateResourceDto(
                resourceId,
                resourceName,
                fileSize,
                projectId
        );
    }

    private static byte[] generateByteArray(int length) {
        byte[] byteArray = new byte[length];
        Random random = new Random();
        random.nextBytes(byteArray);
        return byteArray;
    }
}
