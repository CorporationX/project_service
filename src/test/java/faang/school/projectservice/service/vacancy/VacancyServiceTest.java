package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoReqUpdate;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    private static final Long VACANCY_ID = 1L;
    private static final int DEFAULT_COUNT_CANDIDATES = 5;
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
    private VacancyDtoReqUpdate inputVacancyDtoReqUpdate;

    private String vacancyName;
    private String vacancyDescription;
    private Long projectId;
    private Long createdBy;
    private Long updatedBy;
    private Project project;
    private TeamMember ownerVacancy;
    private TeamMember managerVacancy;
    private Vacancy savedVacancy;

    @BeforeEach
    void setUp() {
        vacancyName = StringValuesForTesting.NAME.getValue();
        vacancyDescription = StringValuesForTesting.DESCRIPTION.getValue();
        projectId = 10L;
        createdBy = 100L;
        updatedBy = 1000L;

        project = Project.builder().id(projectId).build();
        ownerVacancy = TeamMember.builder().roles(List.of(TeamRole.OWNER, TeamRole.DEVELOPER)).build();
        managerVacancy = TeamMember.builder().roles(List.of(TeamRole.MANAGER, TeamRole.DEVELOPER)).build();

        inputVacancyDto = getVacancyDtoForReqCreate();
        savedVacancy = getSavedVacancy();
        inputVacancyDtoReqUpdate = getUpdatedInputVacancyDto();
    }

    @Test
    void testCreateVacancy_WhenInputDtoIsValid() {
        VacancyDto expectedVacancyDto = getExpectedVacancyDto();
        Mockito.when(teamMemberRepository.findById(createdBy)).thenReturn(ownerVacancy);

        Vacancy newVacancy = vacancyMapper.toEntity(inputVacancyDto);
        Vacancy createdVacancy = getSavedVacancy();
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
    void testCreatedVacancy_WhenOwnerRoleCantBeUseToCreate_ShouldThrowException() {
        ownerVacancy = TeamMember.builder().roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER)).build();
        String expectedMessage = MessageFormat.format(ERROR_OWNER_ROLE_FORMAT, createdBy);
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Mockito.when(teamMemberRepository.findById(createdBy)).thenReturn(ownerVacancy);

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.createVacancy(inputVacancyDto));

        assertEquals(expectedMessage, exception.getMessage());
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(createdBy);
    }


    @Test
    void testUpdateVacancy_WhenDataIsValid() {
        vacancyMapper.updateEntityFromDto(inputVacancyDtoReqUpdate, savedVacancy);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        Mockito.when(teamMemberRepository.findById(updatedBy)).thenReturn(managerVacancy);
        Mockito.when(vacancyRepository.save(savedVacancy)).thenReturn(savedVacancy);
        VacancyDto expectedVacancyDto = getExpectedVacancyDtoAfterUpdated();

        VacancyDto result = vacancyService.updateVacancy(VACANCY_ID, inputVacancyDtoReqUpdate);

        assertEquals(expectedVacancyDto, result);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(updatedBy);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(savedVacancy);
    }

    @Test
    void testUpdateVacancy_WhenNeedCloseVacancyAndIsPossible() {
        inputVacancyDtoReqUpdate.setStatus(VacancyStatus.CLOSED);
        vacancyMapper.updateEntityFromDto(inputVacancyDtoReqUpdate, savedVacancy);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        Mockito.when(teamMemberRepository.findById(updatedBy)).thenReturn(managerVacancy);
        Mockito.when(vacancyRepository.save(savedVacancy)).thenReturn(savedVacancy);
        VacancyDto expectedVacancyDto = getExpectedVacancyDtoAfterUpdated();
        expectedVacancyDto.setStatus(VacancyStatus.CLOSED);

        VacancyDto result = vacancyService.updateVacancy(VACANCY_ID, inputVacancyDtoReqUpdate);

        assertEquals(expectedVacancyDto, result);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(updatedBy);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(savedVacancy);
    }

    @Test
    void testUpdateVacancy_WhenNeedCloseVacancyAndIsImpossible_ShouldThrowException() {
        savedVacancy.getCandidates().remove(0);
        inputVacancyDtoReqUpdate.setStatus(VacancyStatus.CLOSED);
        vacancyMapper.updateEntityFromDto(inputVacancyDtoReqUpdate, savedVacancy);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        Mockito.when(teamMemberRepository.findById(updatedBy)).thenReturn(managerVacancy);
        String expectedMessage = MessageFormat.format(VACANCY_CANT_BE_CLOSED_FORMAT, VACANCY_ID, 5);

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.updateVacancy(VACANCY_ID, inputVacancyDtoReqUpdate));

        assertEquals(expectedMessage, exception.getMessage());
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(updatedBy);
    }

    @Test
    void testUpdateVacancy_WhenNotSupportedRoleUserForChanged_ShouldThrowException() {
        TeamMember someTeamMember = TeamMember.builder().roles(List.of(TeamRole.ANALYST)).build();
        vacancyMapper.updateEntityFromDto(inputVacancyDtoReqUpdate, savedVacancy);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        Mockito.when(teamMemberRepository.findById(updatedBy)).thenReturn(someTeamMember);
        String expectedMessage = MessageFormat.format(VACANCY_CANT_BE_CHANGED_FORMAT, someTeamMember.getRoles());

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.updateVacancy(VACANCY_ID, inputVacancyDtoReqUpdate));

        assertEquals(expectedMessage, exception.getMessage());
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(updatedBy);
    }


    @Test
    void testDeleteVacancy() {
        List<TeamMember> members = getTeamMembers(DEFAULT_COUNT_CANDIDATES);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        for (int i = 0; i < members.size(); i++) {
            Mockito.when(teamMemberRepository.findByUserIdAndProjectId(1L + i, projectId))
                    .thenReturn(members.get(i));
        }

        vacancyService.deleteVacancy(VACANCY_ID);

        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(2)).deleteEntity(Mockito.any());
        Mockito.verify(teamMemberRepository, Mockito.times(5))
                .findByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(vacancyRepository, Mockito.times(1)).delete(savedVacancy);
    }

    @Test
    void testDeleteVacancy_WhenVacancyNotFoundById_ShouldThrowException() {
        String expectedMessage = MessageFormat.format(VACANCY_NOT_EXIST_FORMAT, VACANCY_ID);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.deleteVacancy(VACANCY_ID));

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

    private List<TeamMember> getTeamMembers(int count) {
        List<TeamMember> teamMembers = new ArrayList<>(count);
        for (int i = 1; i < count + 1; i++) {
            TeamMember teamMember = TeamMember.builder()
                    .userId((long) i)
                    .build();

            if (i == 1 || i == 2) {
                teamMember.setRoles(List.of(TeamRole.INTERN, TeamRole.ANALYST));
            } else {
                teamMember.setRoles(List.of(TeamRole.ANALYST, TeamRole.DESIGNER));
            }

            teamMembers.add(teamMember);
        }
        return teamMembers;
    }

    private List<Candidate> getCandidates(int count) {
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            candidates.add(Candidate.builder().id(i + 1L).userId(i + 1L).build());
        }
        return candidates;
    }

    private VacancyDto getVacancyDtoForReqCreate() {
        return VacancyDto.builder()
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(projectId)
                .createdBy(createdBy).build();
    }

    private VacancyDto getExpectedVacancyDto() {
        return VacancyDto.builder()
                .vacancyId(VACANCY_ID)
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(projectId)
                .createdBy(createdBy)
                .status(VacancyStatus.OPEN)
                .build();
    }

    private Vacancy getSavedVacancy() {
        return Vacancy.builder()
                .id(VACANCY_ID)
                .name(vacancyName)
                .description(vacancyDescription)
                .project(project)
                .createdBy(createdBy)
                .candidates(getCandidates(DEFAULT_COUNT_CANDIDATES))
                .status(VacancyStatus.OPEN)
                .build();
    }

    private VacancyDto getExpectedVacancyDtoAfterUpdated() {
        Vacancy expectedVacancy = getSavedVacancy();
        expectedVacancy.setName(StringValuesForTesting.UPDATED_NAME.getValue());
        expectedVacancy.setDescription(StringValuesForTesting.UPDATED_DESCRIPTION.getValue());
        expectedVacancy.setUpdatedBy(updatedBy);
        return vacancyMapper.toDto(expectedVacancy);
    }

    private VacancyDtoReqUpdate getUpdatedInputVacancyDto() {
        vacancyName = StringValuesForTesting.UPDATED_NAME.getValue();
        vacancyDescription = StringValuesForTesting.UPDATED_DESCRIPTION.getValue();

        return VacancyDtoReqUpdate.builder()
                .vacancyId(VACANCY_ID)
                .name(vacancyName)
                .description(vacancyDescription)
                .updatedBy(updatedBy)
                .status(VacancyStatus.OPEN)
                .build();
    }
}

enum StringValuesForTesting {
    NAME("Test vacancy name"),
    UPDATED_NAME("Updated vacancy name"),
    DESCRIPTION("Some description of vacancy"),
    UPDATED_DESCRIPTION("Description was updated");

    private final String value;

    StringValuesForTesting(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}