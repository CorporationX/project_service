package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.ProjectDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MomentControllerTest {
    @Mock
    private MomentService momentService;

    @InjectMocks
    private MomentController momentController;

    private ProjectDto expectedProjectDto;

    private MomentDto expectedMomentDto;
    private MomentDto emptyMomentDto;
    private List<MomentDto> expectedMomentDtos;

    private MomentFilterDto expectedMomentFilterDto;

    @BeforeEach
    public void init() {
        expectedMomentDto = new MomentDto();
        emptyMomentDto = new MomentDto();

        expectedMomentFilterDto = new MomentFilterDto();

        expectedProjectDto = new ProjectDto();

        expectedProjectDto.setId(2L);

        expectedMomentDto.setDate(LocalDateTime.now());
        expectedMomentDto.setId(1L);
        expectedMomentDto.setName("feelings");
        expectedMomentDto.setProjectDtos(List.of(
                new ProjectDto(),
                new ProjectDto(),
                new ProjectDto()
        ));

        expectedMomentDtos = List.of(
                expectedMomentDto,
                emptyMomentDto
        );
    }

    @Test
    public void createMomentTest() {
        Mockito.when(momentService.createMoment(expectedMomentDto))
                .thenReturn(new MomentDto());

        MomentDto actualMomentDto = momentController.createMoment(expectedMomentDto);

        assertNotEquals(expectedMomentDto, actualMomentDto);

        Mockito.verify(momentService, Mockito.times(1))
                .createMoment(expectedMomentDto);
    }

    @Test
    public void createMomentNameValidationFailedTest() {
        expectedMomentDto.setName(null);

        MomentDto actualMomentDto = momentController.createMoment(expectedMomentDto);

        assertEquals(expectedMomentDto, actualMomentDto);

        Mockito.verify(momentService, Mockito.times(0))
                .createMoment(expectedMomentDto);
    }

    @Test
    public void createMomentProjectsValidationFailedTest() {
        expectedMomentDto.setProjectDtos(List.of());

        MomentDto actualMomentDto = momentController.createMoment(expectedMomentDto);

        assertEquals(expectedMomentDto, actualMomentDto);

        Mockito.verify(momentService, Mockito.times(0))
                .createMoment(expectedMomentDto);
    }

    @Test
    public void updateMomentTest() {
        Mockito.when(momentService.updateMoment(expectedMomentDto))
                .thenReturn(new MomentDto());

        MomentDto actualMomentDto = momentController.updateMoment(expectedMomentDto);

        assertNotEquals(expectedMomentDto, actualMomentDto);

        Mockito.verify(momentService, Mockito.times(1))
                .updateMoment(expectedMomentDto);
    }

    @Test
    public void updateMomentIdValidationFailedTest() {
        expectedMomentDto.setId(null);

        MomentDto actualMomentDto = momentController.updateMoment(expectedMomentDto);

        assertEquals(expectedMomentDto, actualMomentDto);

        Mockito.verify(momentService, Mockito.times(0))
                .updateMoment(expectedMomentDto);
    }

    @Test
    public void getProjectMomentsTest() {
        Mockito.when(momentService.getProjectMoments(expectedProjectDto, expectedMomentFilterDto))
                .thenReturn(expectedMomentDtos);

        List<MomentDto> actualMomentDtos = momentController.getProjectMoments(expectedProjectDto, expectedMomentFilterDto);

        assertEquals(expectedMomentDtos, actualMomentDtos);

        Mockito.verify(momentService, Mockito.times(1))
                .getProjectMoments(expectedProjectDto, expectedMomentFilterDto);
        Mockito.verify(momentService, Mockito.times(0))
                .getProjectMoments(expectedProjectDto);
    }

    @Test
    public void getProjectMomentsNotSpecifiedFilterTest() {
        Mockito.when(momentService.getProjectMoments(expectedProjectDto))
                .thenReturn(expectedMomentDtos);

        List<MomentDto> actualMomentDtos = momentController.getProjectMoments(expectedProjectDto, null);

        assertEquals(expectedMomentDtos, actualMomentDtos);

        Mockito.verify(momentService, Mockito.times(1))
                .getProjectMoments(expectedProjectDto);
    }

    @Test
    public void getProjectMomentValidationFailedTest() {
        expectedProjectDto.setId(null);

        List<MomentDto> actualMomentDtos = momentController.getProjectMoments(expectedProjectDto, expectedMomentFilterDto);

        assertEquals(List.of(), actualMomentDtos);

        Mockito.verify(momentService, Mockito.times(0))
                .getProjectMoments(expectedProjectDto);
        Mockito.verify(momentService, Mockito.times(0))
                .getProjectMoments(expectedProjectDto, expectedMomentFilterDto);
    }

    @Test
    public void getMomentByIdTest() {
        Mockito.when(momentService.getMomentById(expectedMomentDto.getId()))
                .thenReturn(expectedMomentDto);

        MomentDto actualMomentDto = momentController.getMomentById(expectedMomentDto.getId());

        assertEquals(expectedMomentDto, actualMomentDto);

        Mockito.verify(momentService, Mockito.times(1))
                .getMomentById(expectedMomentDto.getId());
    }
}
