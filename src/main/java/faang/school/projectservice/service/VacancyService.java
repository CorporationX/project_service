package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(vacancy);
    }

    public VacancyDto updateVacancy(Vacancy vacancy) {
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    public void deleteVacancy(Long id) {
        vacancyRepository.deleteById(id);
    }

    public List<Vacancy> getAllVacancies(String name) {
        return vacancyRepository.findAll();
    }

    public VacancyDto getVacancy(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Такой вакансии нет"));
        return vacancyMapper.toDto(vacancy);
    }

}

