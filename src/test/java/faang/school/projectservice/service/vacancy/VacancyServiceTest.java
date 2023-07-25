package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private VacancyService vacancyService;

    private VacancyDto inputVacancyDto;

    private String vacancyName;
    private String vacancyDescription;
    private Long projectId;
    private Long createdBy;

    @BeforeEach
    void setUp() {
        vacancyName = null;
        vacancyDescription = "Some text";
        projectId = 10L;
        createdBy = 1L;
        inputVacancyDto = VacancyDto.builder()
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(projectId)
                .createdBy(createdBy).build();
    }

    @Test
    void testCreateVacancy() {
        VacancyDto expectedVacancyDto = getExpectedVacancyDto();
        Mockito.when(projectRepository.existsById(projectId)).thenReturn(true);
        Mockito.when(teamMemberRepository.findById(createdBy)).thenReturn(
                TeamMember.builder().roles(List.of(TeamRole.OWNER, TeamRole.DEVELOPER)).build());
        Vacancy vacancy = vacancyMapper.toEntity(inputVacancyDto);
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(getVacancyAfterSave());

        VacancyDto result = vacancyService.createVacancy(inputVacancyDto);
        assertEquals(expectedVacancyDto, result);
    }

    private VacancyDto getExpectedVacancyDto() {
        return VacancyDto.builder()
                .vacancyId(1L)
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(projectId)
                .createdBy(createdBy)
                .status(VacancyStatus.OPEN).build();
    }

    private Vacancy getVacancyAfterSave() {
        Project project = Project.builder().id(projectId).build();
        return Vacancy.builder()
                .id(1L)
                .name(vacancyName)
                .description(vacancyDescription)
                .project(project)
                .createdBy(createdBy)
                .status(VacancyStatus.OPEN).build();
    }
}