package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.TeamMemberValidator;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberValidator teamMemberValidator;

    public VacancyDto createVacancy(VacancyDto vacancy) {
        vacancyValidator.validateVacancy(vacancy);
        TeamMember teamMember = teamMemberRepository.findById(vacancy.getCreatedBy());
        teamMemberValidator.validateTeamMember(teamMember);
        Vacancy convertedVacancy = vacancyMapper.toEntity(vacancy);
        return vacancyMapper.toDto(vacancyRepository.save(convertedVacancy));
    }
}