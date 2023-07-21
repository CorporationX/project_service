package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mappper.VacancyMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final VacancyMapper vacancyMapper;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);

        Vacancy savedVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(savedVacancy);
    }

    private void validateVacancy(VacancyDto vacancyDto) {
        TeamMember vacancyCreator = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        List<TeamRole> creatorRoles = vacancyCreator.getRoles();

        if (!projectRepository.existsById(vacancyDto.getProjectId())) {
            throw new DataValidationException("There is no project with this id");
        } else if (!creatorRoles.contains(TeamRole.OWNER) || !creatorRoles.contains(TeamRole.MANAGER)) {
            throw new DataValidationException("The vacancy creator doesn't have the required role");
        }
    }
}
