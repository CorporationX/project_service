package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.VacancyValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.text.MessageFormat;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.INPUT_BODY_IS_NULL;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.PROJECT_NOT_EXIST_FORMAT;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyValidator vacancyValidator;

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        vacancyValidator.validateRequiredFieldsInDTO(vacancyDto);
        Project currProject = projectRepository.getProjectById(vacancyDto.getProjectId());
        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(currProject);
        return vacancyMapper.toDto(vacancyRepository.save(newVacancy));
    }

    private void checkOwnerVacancy(Long creatorId) {
        TeamMember owner = teamMemberRepository.findById(creatorId);
        if (!owner.getRoles().contains(creatorId)) {
            String errorMessage = MessageFormat.format(INPUT_BODY_IS_NULL, creatorId);
            throw new VacancyValidateException(errorMessage);
        }
    }
}
