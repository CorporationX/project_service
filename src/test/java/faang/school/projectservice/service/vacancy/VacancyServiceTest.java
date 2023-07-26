package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.ERROR_OWNER_ROLE_FORMAT;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.NEGATIVE_CREATED_BY_ID_FORMAT;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.NEGATIVE_PROJECT_ID_FORMAT;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.PROJECT_NOT_EXIST_FORMAT;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Spy
    private VacancyValidator vacancyValidator;

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
    private Project project;
    private TeamMember ownerVacancy;

    @BeforeEach
    void setUp() {
        vacancyName = "Vacancy";
        vacancyDescription = "Some text";
        projectId = 10L;
        createdBy = 1L;
        project = Project.builder().id(projectId).build();
        ownerVacancy = TeamMember.builder().roles(List.of(TeamRole.OWNER, TeamRole.DEVELOPER)).build();
        inputVacancyDto = VacancyDto.builder()
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(projectId)
                .createdBy(createdBy).build();
    }

    @Test
    void testCreateVacancy_WhenInputDtoIsValid() {
        VacancyDto expectedVacancyDto = getExpectedVacancyDto();
        Mockito.when(teamMemberRepository.findById(createdBy)).thenReturn(ownerVacancy);

        Vacancy newVacancy = vacancyMapper.toEntity(inputVacancyDto);
        Vacancy createdVacancy = getVacancyAfterSave();
        Mockito.when(vacancyRepository.save(newVacancy)).thenReturn(createdVacancy);

        VacancyDto result = vacancyService.createVacancy(inputVacancyDto);

        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(createdBy);
        Mockito.verify(projectRepository, Mockito.times(1)).getProjectById(projectId);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(newVacancy);
        assertEquals(expectedVacancyDto, result);
    }

    @Test
    void testCreatedVacancy_WhenProjectNotExist_ShouldThrowException() {
        String expectedMessage = MessageFormat.format(PROJECT_NOT_EXIST_FORMAT, projectId);
        Mockito.when(projectRepository.getProjectById(projectId))
                .thenThrow(new EntityNotFoundException(String.format("Project not found by id: %s", projectId)));

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> vacancyService.createVacancy(inputVacancyDto));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCreatedVacancy_WhenOwnerRoleCantBeUse_ShouldThrowException() {
        ownerVacancy = TeamMember.builder().roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER)).build();
        String expectedMessage = MessageFormat.format(ERROR_OWNER_ROLE_FORMAT, createdBy);
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Mockito.when(teamMemberRepository.findById(createdBy)).thenReturn(ownerVacancy);

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.createVacancy(inputVacancyDto));

        assertEquals(expectedMessage, exception.getMessage());
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(createdBy);
    }

    @ParameterizedTest
    @MethodSource("prepareInvalidDto")
    void TestCreateVacancy_WhenInvalidInputDto_ShouldThrowException(VacancyDto inputVacancy, String expectedMessage) {
        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.createVacancy(inputVacancy));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> prepareInvalidDto() {
        VacancyDto DtoWithNullName = VacancyDto.builder().vacancyId(1L).build();
        VacancyDto DtoWithBlankName = VacancyDto.builder().name("").build();

        VacancyDto DtoWithNullDescription = VacancyDto.builder().name("Vacancy").build();
        VacancyDto DtoWithBlankDescription = VacancyDto.builder().name("Vacancy").description("").build();

        VacancyDto DtoWithNullProjectID = VacancyDto.builder().name("Vacancy").description("test").build();
        VacancyDto DtoWithNegativeProjectID = VacancyDto.builder().name("Vacancy").description("test").projectId(-1L).build();
        String errorMessageNegativeProjectId =
                MessageFormat.format(NEGATIVE_PROJECT_ID_FORMAT, DtoWithNegativeProjectID.getProjectId());

        VacancyDto DtoWithNullCreatedBy = VacancyDto.builder().name("Vacancy").description("test").projectId(1L).build();
        VacancyDto DtoWithNegativeCreatedBy = VacancyDto.builder()
                .name("Vacancy")
                .description("test")
                .projectId(1L)
                .createdBy(-1L).build();
        String errorMessageNegativeCreatedBy =
                MessageFormat.format(NEGATIVE_CREATED_BY_ID_FORMAT, DtoWithNegativeCreatedBy.getCreatedBy());

        return Stream.of(
                Arguments.of(DtoWithNullName, ErrorMessagesForVacancy.NAME_IS_NULL),
                Arguments.of(DtoWithBlankName, ErrorMessagesForVacancy.NAME_IS_BLANK),
                Arguments.of(DtoWithNullDescription, ErrorMessagesForVacancy.DESCRIPTION_IS_NULL),
                Arguments.of(DtoWithBlankDescription, ErrorMessagesForVacancy.DESCRIPTION_IS_BLANK),
                Arguments.of(DtoWithNullProjectID, ErrorMessagesForVacancy.PROJECT_ID_IS_NULL),
                Arguments.of(DtoWithNegativeProjectID, errorMessageNegativeProjectId),
                Arguments.of(DtoWithNullCreatedBy, ErrorMessagesForVacancy.CREATED_BY_ID_IS_NULL),
                Arguments.of(DtoWithNegativeCreatedBy, errorMessageNegativeCreatedBy)
        );
    }

    private VacancyDto getExpectedVacancyDto() {
        return VacancyDto.builder()
                .vacancyId(1L)
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(projectId)
                .createdBy(createdBy)
                .status(VacancyStatus.OPEN)
                .build();
    }

    private Vacancy getVacancyAfterSave() {
        return Vacancy.builder()
                .id(1L)
                .name(vacancyName)
                .description(vacancyDescription)
                .project(project)
                .createdBy(createdBy)
                .status(VacancyStatus.OPEN)
                .build();
    }
}