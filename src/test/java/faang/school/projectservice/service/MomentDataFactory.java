package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.CreateMomentRequest;
import faang.school.projectservice.dto.moment.CreateMomentResponse;
import faang.school.projectservice.dto.moment.UpdateMomentRequest;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;

import java.time.LocalDateTime;
import java.util.List;

public class MomentDataFactory {

    public static CreateMomentRequest getCreateMomentRequest() {
        return CreateMomentRequest.builder()
                .name("Test Moment")
                .projectIds(List.of(1L, 2L))
                .build();
    }

    public static CreateMomentResponse getCreateMomentResponse() {
        return CreateMomentResponse.builder()
                .id(100L)
                .name("Test Moment")
                .description("This is a test moment")
                .date(LocalDateTime.of(2024, 2, 15, 10, 0))
                .projectIds(List.of(1L, 2L))
                .resourceIds(List.of(3L, 4L))
                .userIds(List.of(5L, 6L))
                .imageId("test-image-id")
                .createdAt(LocalDateTime.of(2024, 2, 15, 10, 5))
                .updatedAt(LocalDateTime.of(2024, 2, 15, 10, 10))
                .createdBy(1L)
                .updatedBy(2L)
                .build();
    }

    public static UpdateMomentRequest getUpdateMomentRequest() {
        return new UpdateMomentRequest(
                "Updated Moment Name",
                "Updated moment description",
                LocalDateTime.of(2024, 3, 10, 14, 30),
                List.of(1L, 2L, 3L),
                List.of(5L, 6L, 7L),
                "updated-image-id"
        );
    }

    public static Moment getMoment() {
        Moment moment = new Moment();
        moment.setId(100L);
        moment.setName("Test Moment");
        moment.setDescription("This is a test moment");
        moment.setDate(LocalDateTime.of(2024, 2, 15, 10, 0));
        moment.setProjects(List.of(getProject(1L), getProject(2L)));
        moment.setResource(List.of(getResource(3L), getResource(4L)));
        moment.setUserIds(List.of(5L, 6L));
        moment.setImageId("test-image-id");
        moment.setCreatedAt(LocalDateTime.of(2024, 2, 15, 10, 5));
        moment.setUpdatedAt(LocalDateTime.of(2024, 2, 15, 10, 10));
        moment.setCreatedBy(1L);
        moment.setUpdatedBy(2L);
        return moment;
    }

    public static Project getProject(Long id) {
        return Project.builder()
                .id(id)
                .name("Project " + id)
                .description("Test project " + id)
                .build();
    }

    public static Resource getResource(Long id) {
        Resource resource = new Resource();
        resource.setId(id);
        resource.setName("Resource " + id);
        return resource;
    }
}
