package faang.school.projectservice.validation;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exceptions.DataVacancyValidation;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyValidation {
    private final ProjectRepository projectRepository;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyRepository vacancyRepository;

    public boolean validationCreate(VacancyDto vacancyDto) {
        validationProject(vacancyDto);
        validationTutor(vacancyDto);
        return true;
    }

    public void validationProject(VacancyDto vacancyDto) {
        Project projectById = projectRepository.getProjectById(vacancyDto.getProjectId());
        if (projectById.getName() == null || projectById.getName().isBlank()) {
            throw new DataVacancyValidation("Не указано название проекта");
        }
    }

    public void validationTutor(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        if (vacancy.getCreatedBy() == null) {
            throw new DataVacancyValidation("Ответственный не назначен");
        }
        TeamMember tutor = teamMemberRepository.findById(vacancy.getCreatedBy());
        if (!tutor.getRoles().contains(TeamRole.OWNER)) {
            throw new DataVacancyValidation("Данный сотрудник не является куратором");
        }
    }

    public void validationVacancyIfCandidateNeed(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId()).orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));
        int totalVacancies = vacancy.getCount();
        long candidatesApprove = vacancy.getCandidates().stream()
                .filter(candidate -> CandidateStatus.ACCEPTED.equals(candidate.getCandidateStatus()))
                .count();
        if (candidatesApprove < totalVacancies) {
            throw new DataVacancyValidation("Вакансия не может быть закрыта, нужное количество не набрано");
        }
    }

    public void validationVacancyClosed(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId()).orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));

        List<Long> candidates = vacancy.getCandidates().stream().map(candidate -> candidate.getUserId()).toList();
        vacancy.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> candidates.contains(member.getId()))
                .forEach(teamMember -> {
                    if (teamMember.getRoles() == null || teamMember.getRoles().isEmpty()) {
                        throw new DataVacancyValidation("Вакансия не может быть закрыта, пока вся команда не получит свои роли");
                    }
                });
    }
}
