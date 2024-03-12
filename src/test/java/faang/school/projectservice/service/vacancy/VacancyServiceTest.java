package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    VacancyRepository vacancyRepository;
    @Mock
    VacancyMapper vacancyMapper;
    @Mock
    VacancyValidator vacancyValidator;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    List<VacancyFilter> vacancyFilters;
    @InjectMocks
    VacancyService vacancyService;

    private Vacancy vacancy;
    private VacancyDto vacancyDto;
    private Project project;
    private TeamMember curator;

    @BeforeEach
    void setUp() {
        curator = TeamMember.builder()
                .id(1L)
                .userId(6L)
                .build();
        project = Project.builder()
                .id(3L)
                .name("Valid name")
                .description("Valid Description")
                .build();
        vacancy = Vacancy.builder()
                .id(4L)
                .name("Valid name")
                .description("Valid Description")
                .project(project)
                .createdBy(curator.getId())
                .count(2)
                .build();
        vacancyDto = VacancyDto.builder()
                .id(vacancy.getId())
                .name(vacancy.getName())
                .description(vacancy.getDescription())
                .projectId(vacancy.getProject().getId())
                .curatorId(vacancy.getCreatedBy())
                .workersRequired(vacancy.getCount())
                .build();
    }

    @Test
    void create_vacancyCreatedAndSaved_ThenReturnedAsDto() {
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyDto);
        when(vacancyMapper.toEntity(any(VacancyDto.class))).thenReturn(vacancy);

        vacancyService.create(vacancyDto);

        verify(vacancyRepository, times(1)).save(vacancy);
        verify(vacancyMapper, times(1)).toDto(vacancy);
    }
}
