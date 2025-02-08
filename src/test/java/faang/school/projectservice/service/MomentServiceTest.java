package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.moment.*;
import faang.school.projectservice.exception.ProjectAlreadyCanceledException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @Mock
    private MomentRepository momentRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserContext userContext;
    private MomentMapper momentMapper;
    private MomentService momentService;

    @BeforeEach
    void setUp() {
        momentMapper = Mappers.getMapper(MomentMapper.class);
        momentService = new MomentService(momentRepository, projectService, userContext, momentMapper);
    }

    @Test
    void createMoment_ShouldThrowProjectAlreadyCanceledExceptionWhenProjectCanceled() {
        CreateMomentRequest createMomentRequest = MomentDataFactory.getCreateMomentRequest();
        Mockito.when(projectService.getActiveProjectById(Mockito.anyLong()))
                .thenThrow(ProjectAlreadyCanceledException.class);
        Assertions.assertThrows(ProjectAlreadyCanceledException.class,
                () -> momentService.createMoment(createMomentRequest));
    }

    @Test
    void createMoment_Success() {
        List<Project> projects = Arrays.asList(MomentDataFactory.getProject(1L),
                MomentDataFactory.getProject(2L));
        CreateMomentRequest createMomentRequest = MomentDataFactory.getCreateMomentRequest();
        CreateMomentResponse createMomentResponse = MomentDataFactory.getCreateMomentResponse();
        Project project = MomentDataFactory.getProject(1L);
        Mockito.when(projectService.getActiveProjectById(Mockito.anyLong()))
                .thenReturn(project);
        Moment mappedMoment = momentMapper.toEntity(createMomentRequest, projects, 1L);
        Mockito.when(momentRepository.save(Mockito.any(Moment.class)))
                .thenReturn(mappedMoment);
        CreateMomentResponse savedMoment = momentService.createMoment(createMomentRequest);
        Assertions.assertNotNull(savedMoment);
        Assertions.assertEquals(createMomentResponse.name(), savedMoment.name());
    }

    @Test
    void updateMoment_ShouldThrowEntityNotFoundExceptionWhenMomentNotFound() {
        UpdateMomentRequest updateMomentRequest = MomentDataFactory.getUpdateMomentRequest();
        Mockito.when(momentRepository.findById(1L)).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                momentService.updateMoment(1L, updateMomentRequest));
    }

    @Test
    void updateMoment_ShouldNotAddDuplicateUsers() {
        long momentId = 100L;
        UpdateMomentRequest updateMomentRequest = MomentDataFactory.getUpdateMomentRequest();
        Moment existingMoment = MomentDataFactory.getMoment();
        existingMoment.setUserIds(List.of(5L, 6L, 7L));
        Mockito.when(momentRepository.findById(momentId))
                .thenReturn(Optional.of(existingMoment));
        Mockito.when(userContext.getUserId())
                .thenReturn(2L);
        updateMomentRequest.projectIds().forEach(projectId ->
                Mockito.lenient().when(projectService.getActiveProjectById(projectId))
                        .thenReturn(MomentDataFactory.getProject(projectId))
        );
        momentService.updateMoment(momentId, updateMomentRequest);
        Assertions.assertEquals(updateMomentRequest.name(), existingMoment.getName());
        Assertions.assertEquals(updateMomentRequest.description(), existingMoment.getDescription());
        Assertions.assertEquals(updateMomentRequest.date(), existingMoment.getDate());
        Assertions.assertTrue(existingMoment.getUserIds().containsAll(updateMomentRequest.userIds()));
    }

    @Test
    void updateMoment_ShouldUpdateSuccessfully() {
        long momentId = 100L;
        UpdateMomentRequest updateMomentRequest = MomentDataFactory.getUpdateMomentRequest();
        Moment existingMoment = MomentDataFactory.getMoment();
        Mockito.when(momentRepository.findById(momentId))
                .thenReturn(Optional.of(existingMoment));
        Mockito.when(userContext.getUserId())
                .thenReturn(2L);
        updateMomentRequest.projectIds().forEach(projectId ->
                Mockito.when(projectService.getActiveProjectById(projectId))
                        .thenReturn(MomentDataFactory.getProject(projectId))
        );
        momentService.updateMoment(momentId, updateMomentRequest);
        Assertions.assertEquals(updateMomentRequest.name(), existingMoment.getName());
        Assertions.assertEquals(updateMomentRequest.description(), existingMoment.getDescription());
        Assertions.assertEquals(updateMomentRequest.date(), existingMoment.getDate());
        Assertions.assertTrue(existingMoment.getUserIds().containsAll(updateMomentRequest.userIds()));
        Mockito.verify(momentRepository).findById(momentId);
    }

    @Test
    void getMoment_ShouldThrowEntityNotFoundExceptionWhenMomentNotFound() {
        Mockito.when(momentRepository.findById(1L))
                .thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> momentService.getMoment(1L));
    }

    @Test
    void getMoment_ShouldReturnMoment() {
        Moment moment = MomentDataFactory.getMoment();
        GetMomentResponse getMomentResponse = momentMapper.toGetMomentResponse(moment);
        Mockito.when(momentRepository.findById(100L)).thenReturn(Optional.of(moment));
        GetMomentResponse result = momentService.getMoment(100L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(getMomentResponse, result);
    }

    @Test
    void getMoments_ShouldReturnMomentsWhenFilterNull() {
        MomentFilter momentFilter = new MomentFilter(null, null, null);
        List<Moment> moments = List.of(MomentDataFactory.getMoment());
        Mockito.when(momentRepository.findAll(Mockito.any(Specification.class))).thenReturn(moments);
        List<GetMomentResponse> expect = momentMapper.toGetMomentResponseList(moments);
        List<GetMomentResponse> result = momentService.getMoments(momentFilter);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expect, result);
    }

    @Test
    void getMoments_ShouldReturnFilteredMoments() {
        MomentFilter momentFilter = new MomentFilter(1L, 12, 2024); // Используем реальный объект
        List<Moment> moments = List.of(MomentDataFactory.getMoment());
        Mockito.when(momentRepository.findAll(Mockito.any(Specification.class))).thenReturn(moments);
        List<GetMomentResponse> expectedResponses = momentMapper.toGetMomentResponseList(moments);
        List<GetMomentResponse> actualResponses = momentService.getMoments(momentFilter);
        Assertions.assertEquals(expectedResponses.size(), actualResponses.size());
        Assertions.assertEquals(expectedResponses, actualResponses);
    }
}
