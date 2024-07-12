package faang.school.projectservice.service;

import faang.school.projectservice.TestDataFactory;
import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.Long.*;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @InjectMocks
    private MomentService momentService;
    @Mock
    private MomentRepository momentRepository;

    @Test
    void givenMomentWhenCreateMomentThenReturnMoment() {
        // given - precondition
        var momentDto = TestDataFactory.createMomentDto();
        momentDto.getProjects().clear();
        var projectDto = TestDataFactory.createProjectDto();
        projectDto.getMoments().clear();

        var savedMoment = MomentMapper.INSTANCE.toEntity(momentDto);
        var project = TestDataFactory.createProject();

        savedMoment.getProjects().add(project);

        given(momentRepository.save(any(Moment.class)))
                .willReturn(savedMoment);

        // when - action
        var actualResult = momentService.createMoment(momentDto, projectDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getProjects().get(0)).isEqualTo(ProjectMapper.INSTANCE.toDto(project));
        assertThat(actualResult.getUserIds()).containsExactlyInAnyOrderElementsOf(momentDto.getUserIds());

        verify(momentRepository, times(1)).save(any(Moment.class));
        verifyNoMoreInteractions(momentRepository);
    }

    @Test
    void givenMomentAndProjectInvalidStatusWhenCreateMomentThenThrowException() {
        // given - precondition
        var momentDto = TestDataFactory.createMomentDto();
        var invalidProjectDto = TestDataFactory.createProjectDto();
        invalidProjectDto.setStatus(ProjectStatus.COMPLETED);

        // when - action and then - verify the output
        assertThatThrownBy(() -> momentService.createMoment(momentDto, invalidProjectDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot add a moment to a completed project.");

        verifyNoInteractions(momentRepository);
    }

    @Test
    void givenMomentWhenUpdateMomentThenReturnUpdateMoment() {
        // given - precondition
        var momentDto = TestDataFactory.createMomentDto();
        var projectDto = TestDataFactory.createProjectDto();
        projectDto.setId(12L);
        projectDto.setName("Project Name 12");
        projectDto.setDescription("Project Description 12");
        momentDto.getUserIds().add(8L);
        momentDto.getProjects().add(projectDto);

        momentDto.getProjects().add(TestDataFactory.createProjectDto());
        var moment = TestDataFactory.createMoment();
        var savedMoment = MomentMapper.INSTANCE.toEntity(momentDto);

        given(momentRepository.findById(momentDto.getId()))
                .willReturn(of(moment));
        given(momentRepository.save(any(Moment.class)))
                .willReturn(savedMoment);

        // when - action
        var actualResult = momentService.updateMoment(momentDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult. getUserIds())
                .containsExactlyInAnyOrderElementsOf(momentDto.getUserIds());
        assertThat(actualResult.getProjects()).extracting(ProjectDto::getId)
                .containsExactlyElementsOf(momentDto.getProjects().stream()
                                                            .map(ProjectDto::getId)
                                                            .toList());

        verify(momentRepository, times(1)).findById(any(Long.class));
        verify(momentRepository, times(1)).save(moment);
    }


    @Test
    void givenInvalidMomentWhenUpdateMomentThenThrowException() {
        // given - precondition
        var invalidMomentDto = TestDataFactory.createMomentDto();
        invalidMomentDto.setId(MIN_VALUE);

        given(momentRepository.findById(invalidMomentDto.getId()))
                .willReturn(empty());

        // when - action and then - verify the output
        assertThatThrownBy(() -> momentService.updateMoment(invalidMomentDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot find moment");

        verify(momentRepository, times(1)).findById(invalidMomentDto.getId());
        verify(momentRepository, never()).save(any(Moment.class));
    }

    @Test
    void shouldReturnAllMomentsWhenGetAllMomentsIsCalled() {
        // given - precondition
        var momentList = TestDataFactory.createMomentList();
        var expectedResult = TestDataFactory.createMomentDtoList();

        given(momentRepository.findAll()).willReturn(momentList);

        // when - action
        var actualResult = momentService.getAllMoments();

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.size()).isEqualTo(expectedResult.size());
        assertThat(actualResult).extracting(MomentDto::getId)
                .containsExactlyElementsOf(expectedResult.stream()
                                                            .map(MomentDto::getId)
                                                            .toList());
    }

    @Test
    void givenFilterWhenGetAllMomentsThenReturnFilteredMoments() {
        // given - precondition
        var filter = TestDataFactory.createMomentFilterDto();
        var momentList = TestDataFactory.createMomentList();
        var expectedResult = TestDataFactory.createMomentDtoList();

        given(momentRepository.findAll()).willReturn(momentList);

        // when - action
        var actualResult = momentService.getMoments(filter);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.size()).isEqualTo(expectedResult.size());
        assertThat(actualResult).extracting(MomentDto::getId)
                .containsExactlyElementsOf(expectedResult.stream()
                        .map(MomentDto::getId)
                        .toList());
    }

    @Test
    void givenMomentIdWhenGetMomentThenReturnMoment() {
        // given - precondition
        var momentDto = TestDataFactory.createMomentDto();
        var momentDtoId = momentDto.getId();
        var moment = TestDataFactory.createMoment();

        given(momentRepository.findById(momentDtoId))
                .willReturn(of(moment));

        // when - action
        var actualResult = momentService.getMoment(momentDtoId);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(momentDto.getId());
        assertThat(actualResult.getProjects()).containsExactlyInAnyOrderElementsOf(momentDto.getProjects());
        assertThat(actualResult.getUserIds()).containsExactlyInAnyOrderElementsOf(momentDto.getUserIds());
    }
    @Test
    void givenInvalidMomentIdWhenGetMomentThenThrowException() {
        // given - precondition
        var invalidMomentDtoId = MIN_VALUE;

        given(momentRepository.findById(invalidMomentDtoId))
                .willReturn(empty());

        // when - action and then - verify the output
        assertThatThrownBy(() -> momentService.getMoment(invalidMomentDtoId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Moment is not found");

        verify(momentRepository, times(1)).findById(invalidMomentDtoId);
    }
}