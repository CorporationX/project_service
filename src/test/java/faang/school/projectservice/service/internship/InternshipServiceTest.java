package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilter;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import faang.school.projectservice.testData.internship.InternshipTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static faang.school.projectservice.model.InternshipStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;
    @Mock
    private InternshipVerifier internshipVerifier;
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private InternshipMapper internshipMapper;
    private List<InternshipFilter> filters;
    @Mock
    private TeamMemberService teamMemberService;

    private InternshipDto internshipDto;
    private Internship internship;
    private ArgumentCaptor<Internship> internshipArgumentCaptor;

    @BeforeEach
    void setUp() {
        InternshipTestData internshipTestData = new InternshipTestData();
        internshipDto = internshipTestData.getInternshipDto();
        internship = internshipTestData.getInternship();

        internshipArgumentCaptor = ArgumentCaptor.forClass(Internship.class);

        filters = List.of(mock(InternshipFilter.class), mock(InternshipFilter.class));

        internshipService.setFilters(filters);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return filtered internships of project")
        @Test
        void getInternshipsOfProjectTest() {
            InternshipFilterDto filterDto = new InternshipFilterDto();
            internship.setInterns(List.of());
            when(internshipRepository.findAll()).thenReturn(List.of(internship));
            when(internshipMapper.toDto(any(Internship.class))).thenReturn(internshipDto);

            filters.forEach(filter -> {
                when(filter.isApplicable(filterDto)).thenReturn(true);
                when(filter.apply(anyList(), eq(filterDto))).thenReturn(Stream.of(internship));
            });

            internshipService.getInternshipsOfProject(1L, filterDto);

            filters.forEach(filter -> {
                verify(filter).isApplicable(filterDto);
                verify(filter).apply(anyList(), eq(filterDto));
            });
        }

        @DisplayName("should return all internships")
        @Test
        void getAllInternshipsTest() {
            when(internshipRepository.findAll()).thenReturn(List.of(internship));

            assertDoesNotThrow(() -> internshipService.getAllInternships());
        }

        @DisplayName("should return internship when such internship exists")
        @Test
        void getInternshipByIdTest() {
            when(internshipRepository.findById(anyLong())).thenReturn(Optional.ofNullable(internship));

            assertDoesNotThrow(() -> internshipService.getInternshipById(anyLong()));
        }

        @Nested
        class CreateUpdateTests {
            private Project project;
            private TeamMember mentor;
            private List<TeamMember> interns;

            @BeforeEach
            void setUpd() {
                project = internship.getProject();
                mentor = internship.getMentorId();
                interns = internship.getInterns();

                when(projectRepository.getProjectById(internshipDto.getProjectId())).thenReturn(project);
                when(teamMemberRepository.findById(internshipDto.getMentorId())).thenReturn(mentor);
                when(teamMemberRepository.findAllByIds(internshipDto.getInternsIds())).thenReturn(interns);
            }

            @DisplayName("should save created internship when internship is verified")
            @Test
            void createTest() {
                when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

                doNothing().when(internshipVerifier).verifyMentorsProject(project, mentor);
                doNothing().when(internshipVerifier).verifyExistenceOfAllInterns(interns, internshipDto.getInternsIds().size());

                assertDoesNotThrow(() -> internshipService.create(internshipDto));

                verify(internshipRepository).save(internshipArgumentCaptor.capture());
            }

            @DisplayName("should update internship without hiring interns when the updated internship isn't completed")
            @Test
            void updateUncompletedInternshipTest() {
                internshipDto.setId(1L);
                internship.setId(1L);

                when(internshipRepository.findById(internshipDto.getId())).thenReturn(Optional.of(internship));
                doNothing().when(internshipVerifier).verifyUpdatedInterns(internship, interns);
                doNothing().when(internshipMapper).updateEntity(internshipDto, internship);

                assertDoesNotThrow(() -> internshipService.update(internshipDto));

                verify(internshipRepository).save(internshipArgumentCaptor.capture());
            }

            @DisplayName("should update internship with hiring interns when the updated internship iscompleted")
            @Test
            void updateCompletedInternshipTest() {
                internshipDto.setId(1L);
                internshipDto.setStatus(COMPLETED);
                internship.setId(1L);
                internship.setStatus(COMPLETED);

                when(internshipRepository.findById(internshipDto.getId())).thenReturn(Optional.of(internship));
                doNothing().when(internshipVerifier).verifyUpdatedInterns(internship, interns);
                doNothing().when(internshipMapper).updateEntity(internshipDto, internship);

                assertDoesNotThrow(() -> internshipService.update(internshipDto));

                verify(teamMemberService).hireInterns(internship);
                verify(internshipRepository).save(internshipArgumentCaptor.capture());
            }
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("shouldn't return internship when such internship doesn't exist")
        @Test
        void getInternshipByIdTest() {
            when(internshipRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(DataValidationException.class, () -> internshipService.getInternshipById(anyLong()));
        }

        @Nested
        class CreateUpdateTests {
            private Project project;
            private TeamMember mentor;
            private List<TeamMember> interns;

            @BeforeEach
            void setUpd() {
                project = internship.getProject();
                mentor = internship.getMentorId();
                interns = internship.getInterns();

                when(projectRepository.getProjectById(internshipDto.getProjectId())).thenReturn(project);
                when(teamMemberRepository.findById(internshipDto.getMentorId())).thenReturn(mentor);
                when(teamMemberRepository.findAllByIds(internshipDto.getInternsIds())).thenReturn(interns);
            }

            @DisplayName("should throw exception when internship to be created isn't valid")
            @Test
            void createTest() {
                doThrow(new RuntimeException()).when(internshipVerifier).verifyMentorsProject(project, mentor);

                assertThrows(RuntimeException.class, () -> internshipService.create(internshipDto));

                verifyNoInteractions(internshipRepository);
            }

            @DisplayName("should throw exception when internship to be updated isn't valid")
            @Test
            void updateTest() {
                when(internshipRepository.findById(internshipDto.getId())).thenReturn(Optional.of(internship));
                doThrow(new RuntimeException()).when(internshipVerifier).verifyUpdatedInterns(internship, interns);

                assertThrows(RuntimeException.class, () -> internshipService.update(internshipDto));

                verifyNoInteractions(teamMemberService);
                verify(internshipRepository, times(0)).save(internshipArgumentCaptor.capture());
            }
        }
    }
}