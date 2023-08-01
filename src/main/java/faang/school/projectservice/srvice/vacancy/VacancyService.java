package faang.school.projectservice.srvice.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyValidator vacancyValidator;
    private final VacancyMapper vacancyMapper;

    public void deleteVacancy(VacancyDto vacancyDto, long deleterId) {
        vacancyValidator.updateVacancyServiceValidation(vacancyDto, deleterId);
        candidateRepository.deleteAllByIdInBatch(vacancyDto.getCandidates());
        vacancyRepository.deleteById(vacancyDto.getId());
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto, long updaterId) {
        vacancyValidator.updateVacancyServiceValidation(vacancyDto, updaterId);

        Vacancy target = vacancyRepository.findById(vacancyDto.getId()).orElseThrow(() -> new VacancyValidationException("Invalid vacancy!"));
        Vacancy source = vacancyMapper.toEntity(vacancyDto);

        if (source.getStatus().equals(VacancyStatus.CLOSED) && !target.getStatus().equals(VacancyStatus.CLOSED)) {
            if (source.getCount() != 0) {
                throw new VacancyValidationException("Still need employee!");
            }
        }

        source.setUpdatedBy(updaterId);
        source.setUpdatedAt(LocalDateTime.now());
        return vacancyMapper.toDto(vacancyRepository.save(source));
    }
}
