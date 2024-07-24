package handler.initiative;

import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.handler.initiative.InitiativeStatusDoneHandler;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.valitator.initiative.InitiativeStatusDoneValidator;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InitiativeStatusDoneHandlerTest {

    @Mock
    private MomentRepository momentRepository;
    @Mock
    private InitiativeStatusDoneValidator initiaiveStatusDoneValidator;

    @InjectMocks
    private InitiativeStatusDoneHandler initiativeStatusDoneHandler;
    private static final Random RANDOM = new Random();

    @ParameterizedTest
    @MethodSource("handler.initiative.InitiativeStatusDoneHandlerTest#getArgumentsForTestIsApplicableFalse")
    void testIsApplicableFalse(InitiativeStatus initiativeStatusDto, InitiativeStatus initiativeStatus, boolean expectedResult) {
        WriteInitiativeDto writeInitiativeDto = new WriteInitiativeDto(
                1L,
                "dummy",
                "dummy",
                Lists.emptyList(),
                initiativeStatusDto,
                1L
        );
        Initiative initiative = Initiative.builder()
                .status(initiativeStatus)
                .build();

        boolean actualResult = initiativeStatusDoneHandler.isApplicable(writeInitiativeDto, initiative);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void handleSuccess() {
        Initiative initiative = this.createInitiative();

        initiativeStatusDoneHandler.handle(initiative);

        var argumentCaptor = ArgumentCaptor.forClass(Moment.class);
        Mockito.verify(momentRepository, Mockito.times(1)).save(argumentCaptor.capture());
        Moment actualMoment = argumentCaptor.getValue();
        assertThat(actualMoment.getName()).isEqualTo("Выполненная инициатива");
        assertThat(actualMoment.getUserIds()).hasSize(1);
        assertThat(actualMoment.getUserIds().get(0)).isEqualTo(initiative.getCurator().getId());
        assertThat(actualMoment.getProjects()).hasSize(1);
        assertThat(actualMoment.getProjects().get(0).getId()).isEqualTo(initiative.getSharingProjects().get(0).getId());
    }

    static Stream<Arguments> getArgumentsForTestIsApplicableFalse() {
        InitiativeStatus any = InitiativeStatus.values()[RANDOM.nextInt(InitiativeStatus.values().length)];
        InitiativeStatus notDone = InitiativeStatus.IN_PROGRESS;
        return Stream.of(
                Arguments.of(InitiativeStatus.DONE, notDone, true),
                Arguments.of(null, any, false),
                Arguments.of(any, InitiativeStatus.DONE, false),
                Arguments.of(notDone, any, false)
        );
    }

    private Initiative createInitiative() {
        Project project1 = new Project();
        project1.setName("project1");
        project1.setStatus(ProjectStatus.IN_PROGRESS);
        project1.setVisibility(ProjectVisibility.PUBLIC);

        Team team1 = new Team();
        team1.setProject(project1);

        TeamMember teamMember1 = new TeamMember();
        teamMember1.setUserId(1L);
        teamMember1.setTeam(team1);

        Project project2 = new Project();
        project2.setId(1L);
        project2.setName("dummy2");
        project2.setStatus(ProjectStatus.IN_PROGRESS);
        project2.setVisibility(ProjectVisibility.PUBLIC);

        return Initiative.builder()
                .curator(teamMember1)
                .project(project1)
                .name("name1")
                .description("description1")
                .sharingProjects(new ArrayList<>(List.of(project2)))
                .status(InitiativeStatus.OPEN)
                .build();
    }

}