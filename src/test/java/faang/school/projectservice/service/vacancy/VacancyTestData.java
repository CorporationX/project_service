package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class VacancyTestData {

    public static Stream<Vacancy> getVacanciesStream() {
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .name("Alex").build();
        Vacancy vacancy1 = Vacancy.builder()
                .id(2L)
                .name("Evgen").build();
        Vacancy vacancy2 = Vacancy.builder()
                .id(3L)
                .name("Alex").build();
        Vacancy vacancy3 = Vacancy.builder()
                .id(4L)
                .name("Olga").build();
        return Stream.of(vacancy, vacancy1, vacancy2, vacancy3);
    }

    public static List<Candidate> getCandidates() {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        Candidate candidate1 = new Candidate();
        candidate1.setId(2L);
        Candidate candidate2 = new Candidate();
        candidate1.setId(2L);
        return Arrays.asList(candidate, candidate1, candidate2);
    }

    public static VacancyFilterDto getFilterDto() {
        return VacancyFilterDto.builder()
                .id(1L)
                .namePattern("Alex").build();
    }

    public static VacancyDto getVacancyDto() {
        return VacancyDto.builder()
                .id(1L)
                .name("Alex")
                .projectId(1L)
                .candidateIds(List.of(1L, 2L))
                .build();
    }

    public static Vacancy getVacancy(Long id, String name) {
        return Vacancy.builder()
                .id(id)
                .name(name)
                .candidates(new ArrayList<>())
                .build();
    }

    public static List<TeamMember> getTeamMembers() {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setRoles(new ArrayList<>());
        teamMember.getRoles().add(TeamRole.OWNER);
        teamMember.getRoles().add(TeamRole.DESIGNER);
        return List.of(teamMember);
    }
}
