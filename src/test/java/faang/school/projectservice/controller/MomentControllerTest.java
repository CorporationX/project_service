package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentUpdateRequestDto;
import faang.school.projectservice.service.impl.MomentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MomentControllerTest {
    @Mock
    private MomentServiceImpl momentServiceMock;
    @InjectMocks
    private MomentController momentController;
    private MomentCreateRequestDto validMomentCreateRequestDto;
    private MomentUpdateRequestDto validMomentUpdateRequestDto;
    private MomentFilterDto momentFilterDto;

    @BeforeEach
    void init() {
        List<Long> defaultProjectsIds = new ArrayList<>(List.of(1L, 2L, 3L));

        validMomentCreateRequestDto = MomentCreateRequestDto.builder()
                .name("Cool moment")
                .description("some description")
                .projectIds(defaultProjectsIds)
                .build();

        validMomentUpdateRequestDto = MomentUpdateRequestDto.builder()
                .name("Cool moment")
                .description("some description")
                .projectToAddIds(defaultProjectsIds)
                .build();

        momentFilterDto = MomentFilterDto.builder()
                .dateFrom(LocalDateTime.parse("2025-01-01T00:00:00"))
                .dateTo(LocalDateTime.parse("2025-12-31T23:59:59"))
                .projectsIds(defaultProjectsIds)
                .build();
    }

    @Test
    @DisplayName("Test moment creation")
    void testCreate() {
        momentController.create(validMomentCreateRequestDto);
        Mockito.verify(momentServiceMock, Mockito.times(1))
                .createMoment(validMomentCreateRequestDto);
    }

    @Test
    @DisplayName("Test moment update")
    void testUpdate() {
        long momentId = 10000L;
        momentController.update(momentId, validMomentUpdateRequestDto);
        Mockito.verify(momentServiceMock, Mockito.times(1))
                .updateMoment(momentId, validMomentUpdateRequestDto);
    }

    @Test
    @DisplayName("Test get moments by filter")
    void testGetMoments() {
        momentController.getMoments(momentFilterDto);
        Mockito.verify(momentServiceMock, Mockito.times(1)).getMoments(momentFilterDto);
    }

    @Test
    @DisplayName("Test get all moments")
    void testGetAllMoments() {
        momentController.getAllMoments();
        Mockito.verify(momentServiceMock, Mockito.times(1)).getAllMoments();
    }

    @Test
    @DisplayName("Test get one moment")
    void testGetMoment() {
        momentController.getMoment(1L);
        Mockito.verify(momentServiceMock, Mockito.times(1)).getMoment(1L);
    }
}