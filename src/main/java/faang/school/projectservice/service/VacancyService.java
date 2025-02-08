package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository memberRepository;
    private final VacancyValidator validator;
    private final VacancyMapper mapper;

    public VacancyDto createVacancy(VacancyDto dto) {
        Vacancy vacancy = mapper.toEntity(dto);
        validator.validateCreateVacancy(vacancy, memberRepository.findById(vacancy.getCreatedBy()));
        return mapper.toDto(vacancyRepository.save(vacancy));
    }

    public VacancyDto updateVacancy(VacancyDto dto) {
        Vacancy vacancy = mapper.toEntity(dto);
        validator.validateUpdateVacancy(vacancy, memberRepository.findById(vacancy.getCreatedBy()));
        return mapper.toDto(vacancyRepository.save(vacancy));
    }

    public void removeVacancy(Long id) {
        validator.validateRemoveVacancy(vacancyRepository.findById(id));
        vacancyRepository.deleteById(id);
    }

    public List<VacancyDto> filterByPosition(TeamRole role) {
        return vacancyRepository.findAll().stream()
                .filter(vacancy -> vacancy.getPosition() == role)
                .map(mapper::toDto)
                .toList();
    }

    public List<VacancyDto> filterByName(String name) {
        return vacancyRepository.findAll().stream()
                .filter(vacancy -> vacancy.getName().toLowerCase().contains(name.toLowerCase()))
                .map(mapper::toDto)
                .toList();
    }
}
