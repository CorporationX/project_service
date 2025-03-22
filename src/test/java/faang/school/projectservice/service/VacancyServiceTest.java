package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.vacancy.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.vacancy.ValidatorVacancy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static faang.school.projectservice.model.CandidateStatus.ACCEPTED;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.VacancyStatus.CLOSED;
import static faang.school.projectservice.model.VacancyStatus.OPEN;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private UserService userService;

    @Spy
    private VacancyMapperImpl vacancyMapper;

    @Captor
    private ArgumentCaptor<MultipartFile> multipartFileCaptor;
    @Mock
    private ValidatorVacancy validatorVacancy;
    @Mock
    private UserContext userContext;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private VacancyFilter vacancyFilters;

    private static final Long ID = 1L;
    private static final int MAX_IMAGE_SIDE_SIZE = 512;

    private Vacancy vacancy;
    private UserDto userDto;
    private Project project;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() throws IOException {
        project = Project.builder()
                .id(ID)
                .ownerId(ID)
                .build();

        vacancy = Vacancy.builder()
                .id(ID)
                .project(project)
                .build();

        userDto = UserDto.builder()
                .id(ID)
                .email("email")
                .username("lehaps")
                .active(true)
                .build();

        String imageName = "test-image.jpg";
        String contentType = "image/jpeg";

        var bufferedImage = new BufferedImage(1024, 500, BufferedImage.TYPE_INT_RGB);
        var outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        mockFile = new MockMultipartFile(imageName, imageName, contentType, inputStream);
    }

    @Test
    void shouldThrowFindById() {
        Mockito.when(vacancyRepository.findById(ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> vacancyService.addVacancyCover(ID, Mockito.any()));
        verify(vacancyRepository).findById(ID);
    }

    @Test
    void shouldAddVacancyCover() {
        Mockito.when(vacancyRepository.findById(ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(userService.getUser(vacancy.getProject().getOwnerId())).thenReturn(userDto);
        doNothing().when(userService).checkUser(userDto.id());
        Mockito.when(amazonS3Service.uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString()))
                .thenReturn("test-key");
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        vacancyService.addVacancyCover(vacancy.getId(), mockFile);

        assertEquals("test-key", vacancy.getCoverImageKey());
        verify(amazonS3Service).uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString());

        verify(userService).getUser(vacancy.getProject().getOwnerId());
        verify(vacancyRepository).save(vacancy);
        verify(vacancyRepository).findById(ID);
    }

    @Test
    void shouldResizeImage() throws IOException {
        Mockito.when(vacancyRepository.findById(ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(userService.getUser(vacancy.getProject().getOwnerId())).thenReturn(userDto);
        doNothing().when(userService).checkUser(userDto.id());
        Mockito.when(amazonS3Service.uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString()))
                .thenReturn("test-key");
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        vacancyService.addVacancyCover(vacancy.getId(), mockFile);


        verify(amazonS3Service).uploadFile(multipartFileCaptor.capture(), Mockito.anyString());

        MultipartFile file = multipartFileCaptor.getValue();

        BufferedImage image = ImageIO.read(file.getInputStream());

        Assertions.assertTrue(image.getWidth() <= MAX_IMAGE_SIDE_SIZE
                || image.getHeight() <= MAX_IMAGE_SIDE_SIZE);

        verify(userService).getUser(vacancy.getProject().getOwnerId());
        verify(vacancyRepository).save(vacancy);
        verify(vacancyRepository).findById(ID);
    }

    @Test
    void shouldDeleteVacancyCover(){
        vacancy.setCoverImageKey("random");

        Mockito.when(vacancyRepository.findById(ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(userService.getUser(vacancy.getProject().getOwnerId())).thenReturn(userDto);
        doNothing().when(userService).checkUser(userDto.id());

        Assertions.assertNull(vacancyService.deleteVacancyCover(ID).getCoverImageKey());

        verify(userService).getUser(vacancy.getProject().getOwnerId());
        verify(vacancyRepository).findById(ID);
    }

    @Test
    void createVacancyTest_Success(){
        VacancyCreateDto dto = VacancyCreateDto.builder().name("Java Dev").position("DEVELOPER")
                .description("Вакансия для Java-разработчика").projectId(2L).count(3).status("OPEN").build();
        Vacancy vacancyEntity = Vacancy.builder().name("Java Dev").position(DEVELOPER)
                .description("Вакансия для Java-разработчика").project(Project.builder().id(2L).build())
                .count(3).status(OPEN).build();
        VacancyDto expectedDto = VacancyDto.builder().id(1L).name("Java Dev").position("DEVELOPER")
                .description("Вакансия для Java-разработчика").projectId(2L).count(3).status("OPEN").build();

        when(validatorVacancy.validatorTeamRole(dto.getPosition())).thenReturn(true);
        when(validatorVacancy.validatorProjectAvailability(dto.getProjectId())).thenReturn(true);
        when(validatorVacancy.checkRoleCurator(dto)).thenReturn(true);

        when(vacancyMapper.toEntity(dto)).thenReturn(vacancyEntity);
        when(vacancyRepository.save(vacancyEntity)).thenReturn(vacancyEntity);
        when(vacancyMapper.toDtoVacancy(vacancyEntity)).thenReturn(expectedDto);

        VacancyDto result = vacancyService.createVacancy(dto);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
    }

    @Test
    void updateVacancyTest_Success() {
        Long userId = 1L;
        Long vacancyId = 100L;

        VacancyUpdateDto dto = VacancyUpdateDto.builder()
                .id(vacancyId)
                .name("Java Dev")
                .position("DEVELOPER")
                .description("Вакансия для Java-разработчика")
                .projectId(2L)
                .count(3)
                .status("OPEN")
                .build();

        Project project = Project.builder().id(2L).build();
        Vacancy existingVacancy = Vacancy.builder()
                .id(vacancyId)
                .project(project)
                .build();
        Vacancy updatedVacancy = Vacancy.builder()
                .id(vacancyId)
                .name("Java Dev")
                .position(DEVELOPER)
                .description("Вакансия для Java-разработчика")
                .project(project)
                .count(3)
                .status(OPEN)
                .build();
        VacancyDto expectedDto = VacancyDto.builder()
                .id(vacancyId)
                .name("Java Dev")
                .position("DEVELOPER")
                .description("Вакансия для Java-разработчика")
                .projectId(2L)
                .count(3)
                .status("OPEN")
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(validatorVacancy.checkRoleUpdatingUser(userId)).thenReturn(true);
        when(validatorVacancy.checkingNumberCandidates(dto)).thenReturn(true);
        when(vacancyRepository.getById(vacancyId)).thenReturn(existingVacancy);
        when(vacancyRepository.save(existingVacancy)).thenReturn(updatedVacancy);
        when(vacancyMapper.toDtoVacancy(updatedVacancy)).thenReturn(expectedDto);

        VacancyDto result = vacancyService.updateVacancy(dto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userContext).getUserId();
        verify(validatorVacancy).checkRoleUpdatingUser(userId);
        verify(validatorVacancy).checkingNumberCandidates(dto);
        verify(vacancyRepository).getById(vacancyId);
        verify(vacancyMapper).toEntityUpdateVacancy(dto, existingVacancy);
        verify(vacancyRepository).save(existingVacancy);
        verify(vacancyMapper).toDtoVacancy(updatedVacancy);
    }

    @Test
    void deleteVacancy_ShouldDeleteVacancyAndCandidates() {
        Long vacancyId = 1L;
        Vacancy vacancy = new Vacancy();
        Candidate candidate1 = new Candidate();
        candidate1.setId(1L);
        candidate1.setUserId(2L);
        candidate1.setVacancy(vacancy);
        Candidate candidate2 = new Candidate();
        candidate2.setId(2L);
        candidate2.setUserId(3L);
        candidate2.setVacancy(vacancy);
        vacancy.setCandidates(List.of(candidate1, candidate2));

        when(vacancyRepository.getById(vacancyId)).thenReturn(vacancy);

        vacancyService.deleteVacancy(vacancyId);

        verify(vacancyRepository).getById(vacancyId);

        verify(candidateRepository).delete(candidate1);
        verify(candidateRepository).delete(candidate2);

        verify(vacancyRepository).delete(vacancy);
    }

    @Test
    void getFilteredVacancies_WithFilters_ReturnsFilteredVacancies() {
        Project project = Project.builder().id(2L).build();
        Vacancy vacancy1 = Vacancy.builder().id(1L).name("Java Dev").status(OPEN).position(DEVELOPER).project(project).build();
        Vacancy vacancy2 = Vacancy.builder().id(2L).name("Python Dev").status(CLOSED).position(DEVELOPER).project(project).build();
        VacancyDto dto1 = VacancyDto.builder().id(1L).name("Java Dev").build();
        VacancyFilterDto filters = new VacancyFilterDto("OPEN", "DEVELOPER");

        VacancyFilter statusFilter = mock(VacancyFilter.class);
        List<VacancyFilter> filtersList = new ArrayList<>();
        filtersList.add(statusFilter);

        ReflectionTestUtils.setField(vacancyService, "vacancyFilters", filtersList);

        when(statusFilter.isApplicable(filters)).thenReturn(true);
        when(statusFilter.apply(any(), eq(filters)))
                .thenAnswer(inv -> ((Stream<Vacancy>) inv.getArgument(0)).filter(v -> v.getStatus() == OPEN));

        when(vacancyRepository.findAll()).thenReturn(List.of(vacancy1, vacancy2));
        when(vacancyMapper.toDtoVacancy(vacancy1)).thenReturn(dto1);

        List<VacancyDto> result = vacancyService.getFilteredVacancies(filters);

        assertEquals(1, result.size());
        assertEquals(dto1, result.get(0));
        verify(statusFilter).isApplicable(filters);
        verify(statusFilter).apply(any(), eq(filters));
        verify(vacancyMapper, never()).toDtoVacancy(vacancy2);
    }

    @Test
    void getVacancyById_ValidId_ReturnsVacancyDto() {
        Long id = 1L;
        Vacancy vacancy = Vacancy.builder()
                .id(id)
                .name("Java Developer")
                .status(OPEN)
                .build();

        VacancyDto expectedDto = VacancyDto.builder()
                .id(id)
                .name("Java Developer")
                .status("OPEN")
                .build();

        when(vacancyRepository.findById(id)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDtoVacancy(vacancy)).thenReturn(expectedDto);

        VacancyDto result = vacancyService.getVacancyById(id);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getStatus(), result.getStatus());

        verify(vacancyRepository).findById(id);
        verify(vacancyMapper).toDtoVacancy(vacancy);
    }
}