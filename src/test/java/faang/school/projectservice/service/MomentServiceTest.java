package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.ProjectDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.moment.CreatedAtFilter;
import faang.school.projectservice.filter.moment.MomentDescriptionPatternFilter;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @InjectMocks
    private MomentService momentService;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentMapper momentMapper;

    @Mock
    private List<MomentFilter> momentFilters;

    private List<Moment> expectedMoments;

    private MomentDto expectedMomentDto;

    private Moment expectedMoment;

    private MomentFilterDto expectedMomentFilterDto;

    private ProjectDto expectedProjectDto;
    private List<ProjectDto> expectedProjectDtos;

    @BeforeEach
    public void init() {

        expectedMomentFilterDto = new MomentFilterDto();

        expectedMoment = new Moment();

        expectedMoment.setId(1L);
        expectedMoment.setName("feelings");
        expectedMoment.setDescription("Just wanna dance till the morning you down, doesn't you?");
        expectedMoment.setDate(LocalDateTime.now());

        Moment otherMoment = new Moment();

        otherMoment.setId(8L);
        otherMoment.setName("autumn");
        otherMoment.setDate(LocalDateTime.now());
        otherMoment.setDescription("autumn shi soon... Oh! There it is)");

        expectedMoments = List.of(
                expectedMoment,
                otherMoment
        );

        expectedProjectDto = new ProjectDto();

        expectedProjectDto.setId(1L);
        expectedProjectDto.setOwnerId(4L);
        expectedProjectDto.setName("guga");
        expectedProjectDto.setDescription("guga description");
        expectedProjectDto.setCreatedAt(LocalDateTime.of(1, 1, 3, 2, 5));
        expectedProjectDto.setUpdatedAt(LocalDateTime.of(3, 2, 1, 12, 24));


        expectedProjectDtos = List.of(
                expectedProjectDto,
                new ProjectDto(2L, 5L, "jomba", "jomba description", LocalDateTime.of(1, 1, 1, 1, 1), LocalDateTime.of(1, 1, 1, 1, 2), ProjectStatus.IN_PROGRESS)
        );

        expectedMomentDto = new MomentDto();

        expectedMomentDto.setId(expectedMoment.getId());
        expectedMomentDto.setDescription(expectedMoment.getDescription());
        expectedMomentDto.setProjectDtos(expectedProjectDtos);
        expectedMomentDto.setName(expectedMoment.getName());
        expectedMomentDto.setDate(expectedMoment.getDate());
    }

    @Test
    public void createMomentTest() {
        Mockito.when(momentMapper.toEntity(expectedMomentDto))
                .thenReturn(expectedMoment);
        Mockito.when(momentMapper.toDto(expectedMoment))
                .thenReturn(new MomentDto());

        Mockito.when(momentRepository.save(expectedMoment))
                .thenReturn(expectedMoment);

        MomentDto actualMomentDto = momentService.createMoment(expectedMomentDto);

        assertNotEquals(actualMomentDto, expectedMomentDto);

        Mockito.verify(momentMapper, Mockito.times(1))
                .toDto(expectedMoment);
        Mockito.verify(momentMapper, Mockito.times(1))
                .toEntity(expectedMomentDto);
    }

    @Test
    public void createMomentCanceledProjectValidationTest() {
        expectedMomentDto.getProjectDtos().get(0).setStatus(ProjectStatus.CANCELLED);

        String expectedExceptionMessage = String.format(
                "Project with id %s is canceled",
                expectedProjectDto.getId()
        );

        String actualExceptionMessage = assertThrows(DataValidationException.class,
                () -> momentService.createMoment(expectedMomentDto))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(momentMapper, Mockito.times(0))
                .toDto(Mockito.any(Moment.class));
        Mockito.verify(momentMapper, Mockito.times(0))
                .toEntity(Mockito.any(MomentDto.class));

        Mockito.verify(momentRepository, Mockito.times(0))
                .save(Mockito.any(Moment.class));
    }

    @Test
    public void createMomentProjectIdValidationFailedTest() {
        expectedMomentDto.getProjectDtos().get(0).setId(null);

        String expectedExceptionMessage = "Project doesn't have an id";

        String actualExceptionMessage = assertThrows(DataValidationException.class,
                () -> momentService.createMoment(expectedMomentDto))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(momentMapper, Mockito.times(0))
                .toDto(Mockito.any(Moment.class));
        Mockito.verify(momentMapper, Mockito.times(0))
                .toEntity(Mockito.any(MomentDto.class));

        Mockito.verify(momentRepository, Mockito.times(0))
                .save(Mockito.any(Moment.class));
    }

    @Test
    public void getProjectMomentsUsingFilterTest() {
        expectedMomentFilterDto.setMomentDescriptionPattern("ing");

        Mockito.when(projectRepository.existsById(expectedProjectDto.getId()))
                .thenReturn(true);

        Mockito.when(momentRepository.findAllByProjectId(expectedProjectDto.getId()))
                .thenReturn(expectedMoments);

        Mockito.when(momentFilters.stream())
                .thenReturn(Stream.of(
                        new MomentDescriptionPatternFilter(),
                        new CreatedAtFilter()
                ));

        Mockito.when(momentMapper.toDto(expectedMoments.get(0)))
                .thenReturn(expectedMomentDto);

        List<MomentDto> expectedMomentDtos = List.of(
                expectedMomentDto
        );

        List<MomentDto> actualMomentDtos = momentService.getProjectMoments(expectedProjectDto, expectedMomentFilterDto);

        assertEquals(expectedMomentDtos, actualMomentDtos);

        Mockito.verify(momentMapper, Mockito.times(1))
                .toDto(expectedMoment);

        Mockito.verify(projectRepository, Mockito.times(1))
                .existsById(expectedProjectDto.getId());

        Mockito.verify(momentRepository, Mockito.times(1))
                .findAllByProjectId(expectedProjectDto.getId());
    }

    @Test
    public void getProjectMomentsWithoutFilterTest() {

        List<MomentDto> expectedMomentDtos = List.of(
                expectedMomentDto
        );

        Mockito.when(projectRepository.existsById(expectedProjectDto.getId()))
                .thenReturn(true);

        Mockito.when(momentRepository.findAllByProjectId(expectedProjectDto.getId()))
                .thenReturn(List.of(expectedMoment));

        Mockito.when(momentMapper.toDto(expectedMoments.get(0)))
                .thenReturn(expectedMomentDto);

        List<MomentDto> actualMomentDtos = momentService.getProjectMoments(expectedProjectDto);

        assertEquals(expectedMomentDtos, actualMomentDtos);

        Mockito.verify(momentMapper, Mockito.times(1))
                .toDto(expectedMoment);
        Mockito.verify(momentRepository, Mockito.times(1))
                .findAllByProjectId(expectedProjectDto.getId());
        Mockito.verify(projectRepository, Mockito.times(1))
                .existsById(expectedProjectDto.getId());
    }

    @Test
    public void getProjectMomentsThrowsDataValidationException() {
        Mockito.when(projectRepository.existsById(expectedProjectDto.getId()))
                .thenReturn(false);

        String expectedExceptionMessage = String.format(
                "A project with id %s doesn't exist",
                expectedProjectDto.getId()
        );

        String actualExceptionMessageWithFilter = assertThrows(DataValidationException.class,
                () -> momentService.getProjectMoments(expectedProjectDto, new MomentFilterDto()))
                .getMessage();
        String actualExceptionMessageWithoutFilter = assertThrows(DataValidationException.class,
                () -> momentService.getProjectMoments(expectedProjectDto))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessageWithFilter);
        assertEquals(expectedExceptionMessage, actualExceptionMessageWithoutFilter);

        Mockito.verify(projectRepository, Mockito.times(2))
                .existsById(expectedProjectDto.getId());

        Mockito.verify(momentRepository, Mockito.times(0))
                .findAllByProjectId(expectedProjectDto.getId());

        Mockito.verify(momentMapper, Mockito.times(0))
                .toDto(Mockito.any(Moment.class));


    }

    @Test
    public void updateMomentTest() {
        Moment expectedUpdatedMoment = new Moment();
        MomentDto expectedUpdatedMomentDto = new MomentDto();

        Mockito.when(momentRepository.existsById(expectedMomentDto.getId()))
                .thenReturn(true);
        Mockito.when(momentRepository.findById(expectedMomentDto.getId()))
                .thenReturn(Optional.ofNullable(expectedMoment));
        Mockito.when(momentRepository.save(expectedUpdatedMoment))
                .thenReturn(expectedUpdatedMoment);

        Mockito.when(momentMapper.updateEntityFromDto(expectedMomentDto, expectedMoment))
                .thenReturn(expectedUpdatedMoment);
        Mockito.when(momentMapper.toDto(expectedUpdatedMoment))
                .thenReturn(expectedUpdatedMomentDto);

        MomentDto actualUpdatedMomentDto = momentService.updateMoment(expectedMomentDto);

        assertEquals(expectedUpdatedMomentDto, actualUpdatedMomentDto);

        Mockito.verify(momentRepository, Mockito.times(1))
                .existsById(expectedMomentDto.getId());
        Mockito.verify(momentRepository, Mockito.times(1))
                .findById(expectedMomentDto.getId());
        Mockito.verify(momentRepository, Mockito.times(1))
                .save(expectedUpdatedMoment);

        Mockito.verify(momentMapper, Mockito.times(1))
                .toDto(expectedUpdatedMoment);
        Mockito.verify(momentMapper, Mockito.times(1))
                .updateEntityFromDto(expectedMomentDto, expectedMoment);
    }

    @Test
    public void updateMomentThrowsDataValidationExceptionTest() {
        String expectedExceptionMessage = String.format(
                "A moment with id %s doesn't exist",
                expectedMomentDto.getId()
        );

        Mockito.when(momentRepository.existsById(expectedMomentDto.getId()))
                .thenReturn(false);

        String actualExceptionMessage = assertThrows(DataValidationException.class,
                () -> momentService.updateMoment(expectedMomentDto))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(momentRepository, Mockito.times(1))
                .existsById(expectedMomentDto.getId());
        Mockito.verify(momentRepository, Mockito.times(0))
                .findById(expectedMomentDto.getId());
        Mockito.verify(momentRepository, Mockito.times(0))
                .save(Mockito.any(Moment.class));

        Mockito.verify(momentMapper, Mockito.times(0))
                .toDto(Mockito.any(Moment.class));
        Mockito.verify(momentMapper, Mockito.times(0))
                .updateEntityFromDto(expectedMomentDto, expectedMoment);

    }

    @Test
    public void getMomentByIdTest() {

        Mockito.when(momentRepository.existsById(expectedMomentDto.getId()))
                .thenReturn(true);
        Mockito.when(momentRepository.findById(expectedMomentDto.getId()))
                        .thenReturn(Optional.ofNullable(expectedMoment));

        Mockito.when(momentMapper.toDto(expectedMoment))
                .thenReturn(expectedMomentDto);

        MomentDto actualMomentDto = momentService.getMomentById(expectedMomentDto.getId());

        assertEquals(expectedMomentDto, actualMomentDto);

        Mockito.verify(momentRepository, Mockito.times(1))
                .existsById(expectedMomentDto.getId());
        Mockito.verify(momentRepository, Mockito.times(1))
                .findById(expectedMomentDto.getId());

        Mockito.verify(momentMapper, Mockito.times(1))
                .toDto(expectedMoment);
    }

    @Test
    public void getMomentByIdThrowsDataValidationExceptionTest() {
        Long expectedId = 42L;

        String expectedExceptionMessage = String.format(
                "A moment with id %s doesn't exist",
                expectedId
        );

        Mockito.when(momentRepository.existsById(expectedId))
                .thenReturn(false);

        String actualExceptionMessage = assertThrows(DataValidationException.class,
                () -> momentService.getMomentById(expectedId))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(momentRepository, Mockito.times(1))
                .existsById(expectedId);
        Mockito.verify(momentRepository, Mockito.times(0))
                .findById(expectedId);

        Mockito.verify(momentMapper, Mockito.times(0))
                .toDto(Mockito.any(Moment.class));
    }

}
