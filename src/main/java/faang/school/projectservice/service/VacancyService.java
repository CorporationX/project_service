package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberRepository teamMemberRepository;

    public VacancyDto createVacancy(VacancyDto vacancy) {
        vacancyValidator.validateVacancy(vacancy);
        Vacancy convertedVacancy = vacancyMapper.toEntity(vacancy);
        TeamMember teamMember = teamMemberRepository.findById(vacancy.getCreatedBy());
        teamMember.setRoles(List.of(TeamRole.OWNER));
        return vacancyMapper.toDto(vacancyRepository.save(convertedVacancy));
    }
}