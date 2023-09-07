//package faang.school.projectservice.service.vacancyTest;
//
//import faang.school.projectservice.dto.project.VacancyDto;
//import faang.school.projectservice.dto.project.VacancyFilterDto;
//import faang.school.projectservice.mapper.VacancyMapperImpl;
//import faang.school.projectservice.model.TeamMember;
//import faang.school.projectservice.model.TeamRole;
//import faang.school.projectservice.model.Vacancy;
//import faang.school.projectservice.model.VacancyStatus;
//import faang.school.projectservice.repository.ProjectRepository;
//import faang.school.projectservice.repository.TeamMemberRepository;
//import faang.school.projectservice.repository.VacancyRepository;
//import faang.school.projectservice.service.VacancyService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@ExtendWith(MockitoExtension.class)
//class VacancyServiceTest {
//
//    @Mock
//    private ProjectRepository projectRepository;
//
//    @Mock
//    private VacancyRepository vacancyRepository;
//
//    @Mock
//    private TeamMemberRepository teamMemberRepository;
//
//    @Spy
//    private VacancyMapperImpl vacancyMapper;
//
//    @InjectMocks
//    private VacancyService vacancyService;
//
//    private VacancyDto vacancyDto;
//
//    private VacancyFilterDto vacancyFilterDto;
//
//
//    @BeforeEach
//    void setUp() {
//        Vacancy vacancy = new Vacancy();
//        vacancy.setId(1L);
//        vacancy.setDescription("privet");
//        vacancy.setName("A");
//        vacancy.setCandidates(new ArrayList<>());
//        vacancy.setStatus(VacancyStatus.OPEN);
//
//        vacancyMapper = new VacancyMapperImpl();
//        vacancyDto = new VacancyDto(vacancy.getId(), vacancy.getName(), vacancy.getDescription(), 2L, List.of(1L, 2L, 7L),
//                LocalDateTime.now(), VacancyStatus.OPEN, 3L, 4L, 120000.0);
//    }
//
//    @Test
//    void create_HappyPath() {
//        vacancyService.create(vacancyDto);
//        Mockito.verify(vacancyRepository, Mockito.times(1))
//                .save(Mockito.any());
//    }
//
//    @Test
//    public void testCreateVacancyCallFindById() {
//        Mockito.when(teamMemberRepository.findById(3L))
//                        .thenReturn(TeamMember
//                                .builder()
//                                .roles(List.of(TeamRole.OWNER))
//                                .build());
//        Mockito.verify(teamMemberRepository, Mockito.times(1))
//                .findById(3L);
//    }
//
//    @Test
//    public void testCreateVacancyCallExistsById() {
//        Mockito.verify(projectRepository, Mockito.times(1)).existsById(1L);
//    }
//
//    @Test
//    public void testCreateVacancyCallSave() {
//        Mockito.verify(vacancyRepository, Mockito.times(1)).save(Mockito.any());
//    }
//
//    @Test
//    void create_ProjectNotFound() {
//        vacancyService.create(vacancyDto);
//        Mockito.verify(vacancyRepository)
//                .save(vacancyMapper.toEntity(vacancyDto));
//    }
//
//
//    @Test
//    void update() {
//
//    }
//
//    @Test
//    void delete() {
//
//    }
//
//    @Test
//    void getByFilters() {
//
//    }
//
//    @Test
//    void findVacancyById() {
//
//    }
//}