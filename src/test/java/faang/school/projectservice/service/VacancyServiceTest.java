package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.VacancyValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private TeamMemberRepository memberRepository;
    @Mock
    private VacancyValidator validator;
    @Spy
    private VacancyMapper mapper = Mappers.getMapper(VacancyMapper.class);
    @InjectMocks
    private VacancyService service;

    @Test
    public void createVacancy_Success() {
        VacancyDto dto = new VacancyDto(1L, TeamRole.DESIGNER, 2, 13L);

        TeamMember creator = TeamMember.builder()
                .id(13L)
                .userId(13L)
                .nickname("Bob")
                .roles(List.of(TeamRole.OWNER, TeamRole.DEVELOPER, TeamRole.ANALYST))
                .team(new Team())
                .stages(Collections.emptyList())
                .build();

        when(memberRepository.findById(any())).thenReturn(Optional.of(creator));

        service.createVacancy(dto);

        ArgumentCaptor<Vacancy> argumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        verify(vacancyRepository).save(argumentCaptor.capture());
        Vacancy vacancy = argumentCaptor.getValue();

        verify(validator).validateCreateVacancy(vacancy, Optional.of(creator));

        Assertions.assertTrue(vacancy.getId() == dto.id());
        Assertions.assertTrue(vacancy.getPosition() == dto.position());
        Assertions.assertTrue(vacancy.getCount() == dto.count());
        Assertions.assertTrue(vacancy.getCreatedBy() == dto.creatorId());
    }

    @Test
    public void updateVacancy_Success() {
        VacancyDto dto = new VacancyDto(1L, TeamRole.DESIGNER, 2, 13L);

        TeamMember creator = TeamMember.builder()
                .id(13L)
                .userId(13L)
                .nickname("Bob")
                .roles(List.of(TeamRole.OWNER, TeamRole.DEVELOPER, TeamRole.ANALYST))
                .team(new Team())
                .stages(Collections.emptyList())
                .build();

        when(memberRepository.findById(any())).thenReturn(Optional.of(creator));

        service.updateVacancy(dto);

        ArgumentCaptor<Vacancy> argumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        verify(vacancyRepository).save(argumentCaptor.capture());
        Vacancy vacancy = argumentCaptor.getValue();

        verify(validator).validateUpdateVacancy(vacancy, Optional.of(creator));

        Assertions.assertTrue(vacancy.getId() == dto.id());
        Assertions.assertTrue(vacancy.getPosition() == dto.position());
        Assertions.assertTrue(vacancy.getCount() == dto.count());
        Assertions.assertTrue(vacancy.getCreatedBy() == dto.creatorId());
    }

    @Test
    public void deleteVacancy_Success() {
        service.removeVacancy(1L);
        verify(vacancyRepository).deleteById(1L);
    }

    @Test
    public void filterByPosition_Success() {
        when(vacancyRepository.findAll()).thenReturn(List.of(
                Vacancy.builder().id(1L).position(TeamRole.DESIGNER).count(5).build(),
                Vacancy.builder().id(2L).position(TeamRole.DEVELOPER).count(2).build(),
                Vacancy.builder().id(3L).position(TeamRole.ANALYST).count(4).build(),
                Vacancy.builder().id(4L).position(TeamRole.DEVELOPER).count(1).build()
        ));

        Assertions.assertTrue(() -> service.filterByPosition(TeamRole.DEVELOPER).size() == 2);
        Assertions.assertTrue(() -> service.filterByPosition(TeamRole.ANALYST).size() == 1);
    }

    @Test
    public void filterByPosition_emptyList() {
        when(vacancyRepository.findAll()).thenReturn(List.of(
                Vacancy.builder().id(1L).position(TeamRole.DESIGNER).count(5).build(),
                Vacancy.builder().id(3L).position(TeamRole.ANALYST).count(4).build()
        ));

        Assertions.assertTrue(() -> service.filterByPosition(TeamRole.DEVELOPER).isEmpty());
    }

    @Test
    public void filterByName_Success() {
        when(vacancyRepository.findAll()).thenReturn(List.of(
                Vacancy.builder().id(1L).name("Junior Java dev. up to 1 year of experience")
                        .position(TeamRole.DEVELOPER).count(5).build(),
                Vacancy.builder().id(2L).name("Analyst")
                        .position(TeamRole.ANALYST).count(4).build(),
                Vacancy.builder().id(3L).name("Middle Java dev.")
                        .position(TeamRole.DEVELOPER).count(4).build(),
                Vacancy.builder().id(4L).name("Senior Java dev.")
                        .position(TeamRole.DEVELOPER).count(4).build(),
                Vacancy.builder().id(5L).name("Junior+ Java dev. 1-2 years of experience")
                        .position(TeamRole.DEVELOPER).count(5).build()
        ));

        Assertions.assertTrue(() -> service.filterByName("Junior").size() == 2);
        Assertions.assertTrue(() -> service.filterByName("dev.").size() == 4);
        Assertions.assertTrue(() -> service.filterByName("senior").size() == 1);
    }

    @Test
    public void filterByName_emptyList() {
        when(vacancyRepository.findAll()).thenReturn(List.of(
                Vacancy.builder().id(1L).name("Junior Java dev. up to 1 year of experience")
                        .position(TeamRole.DEVELOPER).count(5).build(),
                Vacancy.builder().id(2L).name("Analyst")
                        .position(TeamRole.ANALYST).count(4).build(),
                Vacancy.builder().id(3L).name("Middle Java dev.")
                        .position(TeamRole.DEVELOPER).count(4).build(),
                Vacancy.builder().id(4L).name("Senior Java dev.")
                        .position(TeamRole.DEVELOPER).count(4).build(),
                Vacancy.builder().id(5L).name("Junior+ Java dev. 1-2 years of experience")
                        .position(TeamRole.DEVELOPER).count(5).build()
        ));
        Assertions.assertTrue(() -> service.filterByName("OWNER").isEmpty());
    }
}
