package faang.school.projectservice.utils;

import faang.school.projectservice.model.*;

import java.math.BigInteger;
import java.util.List;

public class ResourcePrepareData {

    public static TeamMember getTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();
    }

    public static Resource getResource() {
        return Resource.builder()
                .project(getProject())
                .status(ResourceStatus.ACTIVE)
                .createdBy(TeamMember.builder()
                        .id(1L)
                        .roles(List.of(TeamRole.ANALYST))
                        .build())
                .size(BigInteger.valueOf(4L))
                .build();
    }

    public static Resource getDeletedResource() {
        return Resource.builder()
                .project(getProject())
                .status(ResourceStatus.DELETED)
                .createdBy(TeamMember.builder()
                        .id(1L)
                        .roles(List.of(TeamRole.ANALYST))
                        .build())
                .createdBy(TeamMember.builder()
                        .id(1L)
                        .roles(List.of(TeamRole.ANALYST))
                        .build())
                .build();
    }

    public static Resource getResultResource() {
        return Resource.builder()
                .size(BigInteger.valueOf(10))
                .status(ResourceStatus.ACTIVE)
                .createdBy(TeamMember.builder()
                        .id(1L)
                        .userId(1L)
                        .roles(List.of(TeamRole.DEVELOPER))
                        .build())
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .storageSize(BigInteger.valueOf(10))
                        .maxStorageSize(BigInteger.valueOf(100))
                        .build())
                .build();
    }

    public static Project getProject() {
        return Project.builder()
                .id(1L)
                .name("project")
                .storageSize(BigInteger.valueOf(10))
                .maxStorageSize(BigInteger.valueOf(100))
                .build();
    }

    public static Project getProjectResult() {
        return Project.builder()
                .id(1L)
                .name("project")
                .storageSize(BigInteger.valueOf(10))
                .maxStorageSize(BigInteger.valueOf(100))
//                .updatedAt(LocalDateTime.now(CLOCK))
                .build();
    }
}
