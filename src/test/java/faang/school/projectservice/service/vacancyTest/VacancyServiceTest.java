package faang.school.projectservice.service.vacancyTest;

import faang.school.projectservice.dto.project.VacancyDto;
import faang.school.projectservice.dto.project.VacancyFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filters.VacancyFilter;
import faang.school.projectservice.filters.VacancyFilterByName;
import faang.school.projectservice.filters.VacancyFilterByPosition;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Spy
    private VacancyMapperImpl vacancyMapper;

    @InjectMocks
    private VacancyService vacancyService;

    private VacancyDto vacancyDto;

    private VacancyFilterDto vacancyFilterDto;
    private Vacancy vacancy;
    private TeamMember teamMember;


    @BeforeEach
    void setUp() {
        vacancy = new Vacancy();
        vacancy.setProject(Project.builder().id(1L).build());
        vacancy.setId(2L);
        vacancy.setDescription("privet");
        vacancy.setName("crud1");
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setSalary(120000.0);
        vacancy.setCreatedBy(1L);
        vacancy.setPosition("DEVELOPER");
        vacancy.setVacancyPlaces(3L);

        vacancyDto = VacancyDto.builder()
                .id(vacancy.getId())
                .name(vacancy.getName())
                .description(vacancy.getDescription())
                .status(VacancyStatus.OPEN)
                .salary(vacancy.getSalary())
                .projectId(vacancy.getProject().getId())
                .createdBy(vacancy.getCreatedBy())
                .position("DEVELOPER")
                .vacancyPlaces(vacancy.getVacancyPlaces())
                .build();


        teamMember = TeamMember.builder()
                .id(vacancyDto.getCreatedBy())
                .roles(List.of(TeamRole.MANAGER))
                .build();
    }

    @Test
    void create() {
        Mockito.when(vacancyRepository.existsById(vacancy.getId()))
                .thenReturn(false);
        Mockito.when(projectRepository.existsById(vacancy.getProject().getId()))
                .thenReturn(false);
        Mockito.when(teamMemberRepository.findById(vacancyDto.getCreatedBy()))
                .thenReturn(teamMember);
        Mockito.when(vacancyMapper.toEntity(vacancyDto))
                .thenReturn(vacancy);

        vacancyService.create(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1))
                .save(vacancyMapper.toEntity(vacancyDto));
    }
    @Test
    void delete_HappyPath() {
        Mockito.when(vacancyRepository.findById(vacancyDto.getId()))
                .thenReturn(Optional.of(vacancy));

        vacancyService.delete(vacancyDto.getId());
        Mockito.verify(vacancyRepository, Mockito.times(1))
                .delete(vacancy);
    }

    @Test
    void delete_EntityNotFoundException() {
        Mockito.when(vacancyRepository.findById(vacancyDto.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacancyService.delete(vacancyDto.getId()));
    }

    @Test
    void getByFilters() {
        Vacancy vacancy2 = new Vacancy();
        vacancy2.setProject(Project.builder().id(1L).build());
        vacancy2.setId(2L);
        vacancy2.setDescription("privet2");
        vacancy2.setName("crud1");
        vacancy2.setStatus(VacancyStatus.OPEN);
        vacancy2.setSalary(120000.0);
        vacancy2.setPosition("DEVELOPER");
        vacancy2.setCreatedBy(1L);

        Vacancy vacancy3 = new Vacancy();
        vacancy3.setProject(Project.builder().id(1L).build());
        vacancy3.setId(1L);
        vacancy3.setDescription("privet3");
        vacancy3.setName("crud2");
        vacancy3.setStatus(VacancyStatus.OPEN);
        vacancy3.setPosition("DEVELOPER");
        vacancy3.setSalary(120000.0);
        vacancy3.setCreatedBy(1L);

        Vacancy vacancy4 = new Vacancy();
        vacancy4.setProject(Project.builder().id(1L).build());
        vacancy4.setId(1L);
        vacancy4.setDescription("privet");
        vacancy4.setName("crud3");
        vacancy4.setStatus(VacancyStatus.CLOSED);
        vacancy4.setPosition("DEVELOPER");
        vacancy4.setSalary(120000.0);
        vacancy4.setCreatedBy(1L);

        List<Vacancy> vacancies = List.of(vacancy, vacancy2, vacancy3, vacancy4);

        Mockito.when(vacancyRepository.findAll()).thenReturn(vacancies);
        List<VacancyFilter> filters = List.of(new VacancyFilterByName(), new VacancyFilterByPosition());

        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();
        vacancyFilterDto.setName("crud1");
        vacancyFilterDto.setPosition("DEVELOPER");

        List<VacancyDto> filteredProjectsExpected = List.of(vacancyMapper.toDto(vacancy), vacancyMapper.toDto(vacancy2));
        vacancyService = VacancyService.builder().vacancyMapper(vacancyMapper).vacancyRepository(vacancyRepository).filters(filters).build();

        List<VacancyDto> projectsWithFilterActual = vacancyService.getByFilters(vacancyFilterDto);
        assertEquals(filteredProjectsExpected, projectsWithFilterActual);
    }

    @Test
    void findVacancyById_HappyPath() {
        Mockito.when(vacancyRepository.findById(vacancyDto.getId()))
                .thenReturn(Optional.of(vacancy));

        VacancyDto vacancyById = vacancyService.findVacancyById(vacancyDto.getId());
        assertEquals(vacancyDto, vacancyById);
    }

    @Test
    void findVacancyById_EntityNotFoundException() {
        Mockito.when(vacancyRepository.findById(vacancyDto.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacancyService.findVacancyById(vacancyDto.getId()));
    }
}