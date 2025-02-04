package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.MomentFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MomentServiceImplTest {
    @Mock
    MomentRepository momentRepositoryMock;
    @Mock
    ProjectRepository projectRepositoryMock;
    @InjectMocks
    MomentServiceImpl momentService;
    @Spy
    MomentMapperImpl momentMapper;
    @Mock
    MomentServiceValidator momentServiceValidator;
    MomentCreateRequestDto validMomentCreateRequestDto;
    MomentCreateRequestDto savedMomentResponseDto;
    MomentCreateRequestDto emptyProjectMomentCreateRequestDto;
    MomentCreateRequestDto incorrectDateMomentCreateRequestDto;
    List<Long> defaultProjectsIds;
    List<MomentFilter> momentFilters = new ArrayList<>();
    MomentFilterDto momentFilterDto;

    @BeforeEach
    void setUp() {
        defaultProjectsIds = new ArrayList<>(List.of(1L, 2L, 3L));
        List<Long> emptyProjectsIds = new ArrayList<>();

        validMomentCreateRequestDto = MomentCreateRequestDto.builder()
                .name("Cool moment")
                .date(LocalDateTime.now())
                .description("some description")
                .projectIds(defaultProjectsIds)
                .build();
        emptyProjectMomentCreateRequestDto = MomentCreateRequestDto.builder()
                .name("Cool moment")
                .date(LocalDateTime.now())
                .description("some description")
                .projectIds(emptyProjectsIds)
                .build();
        incorrectDateMomentCreateRequestDto = MomentCreateRequestDto.builder()
                .name("Cool moment")
                .date(LocalDateTime.now())
                .description("some description")
                .projectIds(emptyProjectsIds)
                .build();
        savedMomentResponseDto = MomentCreateRequestDto.builder()
                .name("Cool moment")
                .date(LocalDateTime.now())
                .description("some description")
                .projectIds(defaultProjectsIds)
                .build();
        momentFilterDto = MomentFilterDto.builder()
                .dateFrom(LocalDateTime.MIN)
                .dateTo(LocalDateTime.MAX)
                .projectsIds(defaultProjectsIds)
                .build();

        momentService = new MomentServiceImpl(momentRepositoryMock,
                momentMapper,
                momentFilters,
                momentServiceValidator,
                projectRepositoryMock);
    }

    @Test
    @DisplayName("Test Create Moment")
    void createMoment() {
        Moment moment = momentMapper.toMomentEntity(savedMomentResponseDto);
        Mockito.when(momentRepositoryMock.save(moment)).thenReturn(moment);
        momentService.createMoment(savedMomentResponseDto);
        Mockito.verify(momentRepositoryMock, Mockito.times(1)).save(Mockito.any(Moment.class));
    }

    @Test
    @DisplayName("Test Update Moment")
    void updateMoment() {
        Moment moment = momentMapper.toMomentEntity(validMomentCreateRequestDto);
        Mockito.when(momentRepositoryMock.save(moment)).thenReturn(moment);
        momentService.createMoment(validMomentCreateRequestDto);
        Mockito.verify(momentRepositoryMock, Mockito.times(1)).save(Mockito.any(Moment.class));
    }

    @Test
    @DisplayName("Test get moments")
    void getMoments() {
        momentService.getMoments(momentFilterDto);
        Mockito.verify(momentRepositoryMock, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test Get All moments")
    void getAllMoments() {
        momentService.getAllMoments();
        Mockito.verify(momentRepositoryMock, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test Get Moment")
    void getMoment() {
        Moment moment = momentMapper.toMomentEntity(savedMomentResponseDto);
        Mockito.when(momentRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(moment));
        momentService.getMoment(1L);
        Mockito.verify(momentRepositoryMock, Mockito.times(1)).findById(1L);
    }
}