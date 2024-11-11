package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.filter.moment.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.moment.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    private static final Long MOMENT_ID = 1L;
    private static final Long PROJECT_ID = 1L;
    private static final Long TEAM_MEMBER_ID = 1L;
    private static final String MOMENT_NAME = "Moment Name";

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private MomentMapper momentMapper;

    @Mock
    private List<Filter<MomentFilterDto, Moment>> momentFilters;

    @Mock
    private ProjectService projectService;

    @Mock
    private MomentValidator momentValidator;

    @InjectMocks
    private MomentService momentService;

    private MomentDto momentDto;
    private Moment moment;
    private Project project;
    private Team team;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        teamMember = new TeamMember();
        teamMember.setId(TEAM_MEMBER_ID);
        teamMember.setUserId(1L);

        team = new Team();
        team.setId(1L);
        team.setTeamMembers(List.of(teamMember));

        project = new Project();
        project.setId(PROJECT_ID);
        project.setTeams(List.of(team));

        moment = new Moment();
        moment.setId(MOMENT_ID);
        moment.setName(MOMENT_NAME);
        moment.setProjects(List.of(project));

        momentDto = MomentDto.builder()
                .name(MOMENT_NAME)
                .projectIds(List.of(PROJECT_ID))
                .build();
    }

    @Nested
    @DisplayName("Create Moment Tests")
    class CreateMomentTests {

        @Test
        @DisplayName("When valid MomentDto is provided, it should create a moment")
        void whenValidMomentDtoProvidedThenCreateMoment() {
            when(momentMapper.toEntity(momentDto)).thenReturn(moment);
            when(projectService.getProjectByIds(momentDto.getProjectIds())).thenReturn(List.of(project));
            when(momentRepository.save(any(Moment.class))).thenReturn(moment);
            when(momentMapper.toDto(any(Moment.class))).thenReturn(momentDto);

            MomentDto result = momentService.createMoment(momentDto);

            verify(momentRepository).save(moment);
            assertEquals(momentDto, result);
        }
    }

    @Nested
    @DisplayName("Get Moment Tests")
    class GetMomentTests {

        @Test
        @DisplayName("When Moment ID is valid then return the MomentDto")
        void whenMomentIdIsValidThenReturnMomentDto() {
            when(momentRepository.findById(MOMENT_ID)).thenReturn(Optional.of(moment));
            when(momentMapper.toDto(moment)).thenReturn(momentDto);

            MomentDto result = momentService.getMomentDtoById(MOMENT_ID);

            assertEquals(momentDto, result);
        }

        @Test
        @DisplayName("When Moment ID is invalid then throw EntityNotFoundException")
        void whenMomentIdIsInvalidThenThrowException() {
            when(momentRepository.findById(MOMENT_ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> momentService.getMomentDtoById(MOMENT_ID));
        }
    }

    @Nested
    @DisplayName("Update Moment Tests")
    class UpdateMomentTests {

        @BeforeEach
        void setUp() {
            momentDto.setId(MOMENT_ID);
        }

        @Test
        @DisplayName("When valid moment ID and DTO are provided then updateCampaign the moment")
        void whenValidMomentIdAndDtoProvidedThenUpdateMoment() {
            when(momentRepository.findById(MOMENT_ID)).thenReturn(Optional.of(moment));
            when(projectService.getProjectByIds(momentDto.getProjectIds())).thenReturn(List.of(project));
            when(momentRepository.save(any(Moment.class))).thenReturn(moment);
            when(momentMapper.toDto(any(Moment.class))).thenReturn(momentDto);

            MomentDto result = momentService.updateMoment(MOMENT_ID, momentDto);

            verify(momentRepository).save(moment);
            assertEquals(momentDto, result);
        }
    }

    @Nested
    @DisplayName("Filter Moments Tests")
    class FilterMomentsTests {

        @Mock
        private Filter<MomentFilterDto, Moment> mockMomentFilter;

        @Test
        @DisplayName("When applicable filters are provided then apply them to the moments")
        void whenApplicableFiltersProvidedThenApplyToMoments() {
            MomentFilterDto filterDto = new MomentFilterDto();
            List<Moment> moments = List.of(moment);

            when(momentRepository.findAllByProjectId(PROJECT_ID)).thenReturn(moments);
            when(mockMomentFilter.isApplicable(filterDto)).thenReturn(true);
            when(mockMomentFilter.applyFilter(any(Stream.class), eq(filterDto))).thenReturn(moments.stream());
            when(momentFilters.stream()).thenReturn(Stream.of(mockMomentFilter));
            when(momentMapper.toDto(any(Moment.class))).thenReturn(momentDto);

            List<MomentDto> result = momentService.filterBy(PROJECT_ID, filterDto);

            assertEquals(1, result.size());
            assertEquals(momentDto, result.get(0));
        }
    }
}
