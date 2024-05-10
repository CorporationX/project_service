package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.validation.ValidationTeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final TeamService teamService;
    private final VacancyMapper vacancyMapper;
    private final ValidationTeamMember validationTeamMember;

    public VacancyDto createVacancy(Long creatorId, VacancyDto vacancyDto) {
        validationTeamMember.checkThatTheUserCanCreateAVacancy(teamService.findMemberById(creatorId));
        Vacancy vacancyEntity = vacancyMapper.toEntity(vacancyDto);
        vacancyEntity.setCreatedAt(LocalDateTime.now());
        vacancyEntity.setUpdatedAt(LocalDateTime.now());
        vacancyEntity.setCreatedBy(creatorId);
        vacancyEntity.setStatus(VacancyStatus.OPEN);
        vacancyEntity.setCreatedBy(creatorId);

        return vacancyMapper.toDto(vacancyRepository.save(vacancyEntity));
    }
}
