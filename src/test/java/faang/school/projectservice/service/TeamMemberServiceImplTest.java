package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceImplTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberServiceImpl teamMemberService;

    @Test
    public void shouldValidateAndGetAuthor_throw_whenAuthorIdIsNotPresented() {
        var authorId = 0L;
        var requestDto = createOpenVacancyRequestDto(1, authorId);
        when(teamMemberRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> teamMemberService.validateAndGetAuthor(requestDto));
    }

    @ParameterizedTest
    @MethodSource("getInvalidAuthorRoles")
    public void shouldValidateAndGetAuthor_throw_whenSomeAuthorRolesAreInvalid(List<TeamRole> authorRoles) {
        var authorId = 1L;
        var requestDto = createOpenVacancyRequestDto(1, authorId);
        var author = TeamMember.builder().id(authorId).roles(authorRoles).build();
        when(teamMemberRepository.findById(authorId)).thenReturn(Optional.of(author));

        assertThrows(DataValidationException.class, () -> teamMemberService.validateAndGetAuthor(requestDto));
    }

    private static List<Arguments> getInvalidAuthorRoles() {
        return List.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(TeamRole.ANALYST)),
                Arguments.of(List.of(TeamRole.INTERN, TeamRole.DESIGNER)));
    }

    @ParameterizedTest
    @MethodSource("getValidAuthorRoles")
    public void shouldValidateAndGetAuthor_success_whenAllAuthorRolesAreValid(List<TeamRole> authorRoles) {
        var authorId = 1L;
        var requestDto = createOpenVacancyRequestDto(1, authorId);
        var author = TeamMember.builder().id(authorId).roles(authorRoles).build();
        when(teamMemberRepository.findById(authorId)).thenReturn(Optional.of(author));

        assertDoesNotThrow(() -> teamMemberService.validateAndGetAuthor(requestDto));
    }

    private static List<Arguments> getValidAuthorRoles() {
        return List.of(
                Arguments.of(List.of(TeamRole.OWNER)),
                Arguments.of(List.of(TeamRole.MANAGER)),
                Arguments.of(List.of(TeamRole.OWNER, TeamRole.MANAGER)));
    }

    private static OpenVacancyRequestDto createOpenVacancyRequestDto(long projectId, long authorId) {
        return OpenVacancyRequestDto.builder()
                .name("Test name")
                .description("Test description")
                .projectId(projectId)
                .position(TeamRole.ANALYST)
                .requiredCandidatesCount(1)
                .authorId(authorId)
                .build();
    }
}