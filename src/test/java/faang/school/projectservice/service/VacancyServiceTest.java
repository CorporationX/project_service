package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.VacancyCandidateMapper;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import org.hibernate.validator.constraints.ModCheck;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static faang.school.projectservice.model.TeamRole.DESIGNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    private VacancyCandidateDto vacancyCandidateDto;
    private VacancyDto vacancyDto;
    private Vacancy vacancy;
    private VacancyFilterDto filterDto;
    private VacancyFilter filter;
    private final List<VacancyFilter> filters = List.of();

    @Mock
    private VacancyRepository repository;

    @Mock
    TeamMemberRepository memberRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    private UserContext context;

    @Spy
    private VacancyMapper mapper;

    @Mock
    private VacancyCandidateMapper candidateMapper;

    @InjectMocks
    VacancyService service;


    @Test
    public void positiveCreateVacancy() {

        Project project = new Project();
        project.setId(1L);
        project.setName("HODL");

        VacancyDto vacancyDto = VacancyDto.builder()
                .vacancyId(1L)
                .name("Designer")
                .salary(10.5)
                .count(5)
                .project(project)
                .build();

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("Designer");
        vacancy.setSalary(10.5);
        vacancy.setCount(5);
        vacancy.setProject(project);

        // Настройка моков
        VacancyMapper mapper = mock(VacancyMapper.class);
        when(mapper.toEntity(vacancyDto)).thenReturn(vacancy);

        // Вызов метода
        //VacancyService vacancyService = new VacancyService(
                //repository, context, mapper, candidateMapper, filters, memberRepository, projectRepository);
        //vacancyService.createVacancy(vacancyDto);

        // Проверка вызова метода
        verify(mapper).toEntity(vacancyDto);
    }


    @Test
    public void negativeUserNotFound() {
        assertThrows(DataValidationException.class, () -> service.createVacancy(vacancyDto));
    }

    @Test
    public void negativeNoHaveRequiredRole() {
    }

    @Test
    public void negativeProjectNotFound() {
    }

    @Test
    public void negativeNameIsBlank() {
    }

    @Test
    public void negativeSalaryIsZero() {
    }

    @Test
    public void negativeVacanciesCountZero() {
    }

    @Test
    public void positiveFindVacancy() {
        List<Vacancy> vacancyList = new ArrayList<>();
        when(repository.findAll()).thenReturn(vacancyList);
        List<VacancyCandidateDto> result = service.findVacancy(filterDto);
        assertNotNull(result);
        verify(repository, times(1)).findAll();
    }

    @Test
    public void positiveGetInfoAboutVacancy() {
        long vacancyId = 1L;
        Vacancy infoVacancy = Vacancy.builder().id(vacancyId).name("vacancy").build();
        when(repository.findById(vacancyId)).thenReturn(Optional.ofNullable(infoVacancy));
        VacancyCandidateDto test = candidateMapper.toDto(infoVacancy);
        when(candidateMapper.toDto(infoVacancy)).thenReturn(test);


        assertNotNull(infoVacancy);
        assertNotNull(test);
        verify(repository, times(1)).findById(vacancyId);
    }


}


