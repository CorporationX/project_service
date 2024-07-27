package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.util.TestDataFactory;
import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

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
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MomentMapper momentMapper;

    @Test
    void givenMomentWhenCreateMomentThenReturnMoment() {
        // given - precondition
        var momentDto = TestDataFactory.createMomentDto();

        var projectId = TestDataFactory.createProjectDto().getId();

        var moment = TestDataFactory.createMoment();
        var project = TestDataFactory.createProject();

        moment.getProjects().add(project);

        var savedMomentDto = TestDataFactory.createMomentDto();
        savedMomentDto.getProjects().add(projectId);

        given(projectRepository.getProjectById(projectId))
                .willReturn(project);
        given(momentMapper.toEntity(momentDto))
                .willReturn(moment);
        given(momentRepository.save(any(Moment.class)))
                .willReturn(moment);
        given(projectRepository.save(any(Project.class)))
                .willReturn(project);
        given(momentMapper.toDto(any(Moment.class)))
                .willReturn(savedMomentDto);

        // when - action
        var actualResult = momentService.createMoment(momentDto, projectId);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getProjects()).containsExactlyInAnyOrderElementsOf(savedMomentDto.getProjects());
        assertThat(actualResult.getUserIds()).containsExactlyInAnyOrderElementsOf(momentDto.getUserIds());

        verify(momentRepository, times(1)).save(any(Moment.class));
        verify(projectRepository, times(1)).save(any(Project.class));
        verifyNoMoreInteractions(momentRepository);
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void givenMomentAndProjectInvalidStatusWhenCreateMomentThenThrowException() {
        // given - precondition
        var momentDto = TestDataFactory.createMomentDto();
        var invalidProjectId = TestDataFactory.createProjectDto().getId();
        var invalidProject = TestDataFactory.createProject();
        invalidProject.setStatus(ProjectStatus.COMPLETED);

        given(projectRepository.getProjectById(invalidProjectId))
                .willReturn(invalidProject);

        // when - action and then - verify the output
        assertThatThrownBy(() -> momentService.createMoment(momentDto, invalidProjectId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot add a moment to a completed project.");

        verifyNoInteractions(momentRepository);
    }
    @Test
    void givenMomentAndProjectInvalidIdWhenCreateMomentThenThrowException() {
        // given - precondition
        var momentDto = TestDataFactory.createMomentDto();
        var invalidProjectId = TestDataFactory.createProjectDto().getId();

        // when - action and then - verify the output
        given(projectRepository.getProjectById(invalidProjectId))
                .willReturn(null);

        // when - action and then - verify the output
        assertThatThrownBy(() -> momentService.createMoment(momentDto, invalidProjectId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");

        verifyNoInteractions(momentRepository);
    }

    @Test
    void givenMomentWhenUpdateMomentThenReturnUpdateMoment() {
        // given - precondition
        var momentDto = TestDataFactory.createMomentDto();
        var project = TestDataFactory.createProject();
        project.setId(12L);
        project.setName("Project Name 12");
        project.setDescription("Project Description 12");
        momentDto.getUserIds().add(8L);
        momentDto.getProjects().add(project.getId());

        var moment = TestDataFactory.createMoment();

        var savedMoment = TestDataFactory.createMoment();
        savedMoment.getUserIds().add(8L);
        savedMoment.getProjects().add(project);

        given(momentRepository.findById(momentDto.getId()))
                .willReturn(of(moment));
        given(projectRepository.getProjectById(anyLong()))
                .willReturn(project);
        given(momentRepository.save(moment))
                .willReturn(savedMoment);
        given(momentMapper.toDto(savedMoment))
                .willReturn(momentDto);

        // when - action
        var actualResult = momentService.updateMoment(momentDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult. getUserIds())
                .containsExactlyInAnyOrderElementsOf(momentDto.getUserIds());
        assertThat(actualResult.getProjects())
                .containsExactlyInAnyOrderElementsOf(momentDto.getProjects());

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

        given(momentRepository.findAll())
                .willReturn(momentList);
        given(momentMapper.toDto(momentList.get(0)))
                .willReturn(expectedResult.get(0));

        // when - action
        var actualResult = momentService.getAllMoments();

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.size()).isEqualTo(expectedResult.size());
        assertThat(actualResult).extracting(MomentDto::getId)
                .containsExactlyInAnyOrderElementsOf(expectedResult.stream()
                        .map(MomentDto::getId)
                        .toList());
    }

    @Test
    void givenFilterWhenGetAllMomentsThenReturnFilteredMoments() {
        // given - precondition
        MomentFilter filterMock = mock(MomentFilter.class);
        List<MomentFilter> momentFilters = List.of(filterMock);
        momentService = new MomentService(momentRepository, projectRepository, momentMapper, momentFilters);

        var filter = TestDataFactory.createMomentFilterDto();
        var momentList = TestDataFactory.createMomentList();
        var expectedResult = TestDataFactory.createMomentDtoList();

        given(momentRepository.findAll())
                .willReturn(momentList);
        given(momentFilters.get(0).isApplicable(filter))
                .willReturn(true);
        given(momentFilters.get(0).getApplicableFilters(any(Stream.class), any(MomentFilterDto.class)))
                .willReturn(momentList.stream());
        given(momentMapper.toDto(momentList.get(0)))
                .willReturn(expectedResult.get(0));

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
        given(momentMapper.toDto(moment))
                .willReturn(momentDto);

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