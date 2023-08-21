package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.ERROR_OWNER_ROLE_FORMAT;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Project curProject = projectRepository.getProjectById(vacancyDto.getProjectId());
        checkOwnerVacancy(vacancyDto.getCreatedBy());

        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(curProject);
        return vacancyMapper.toDto(vacancyRepository.save(newVacancy));
    }

    private void checkOwnerVacancy(Long creatorId) {
        TeamMember owner = teamMemberRepository.findById(creatorId);
        if (!owner.getRoles().contains(TeamRole.OWNER)) {
            String errorMessage = MessageFormat.format(ERROR_OWNER_ROLE_FORMAT, creatorId);
            throw new VacancyValidateException(errorMessage);
        }
    }
}