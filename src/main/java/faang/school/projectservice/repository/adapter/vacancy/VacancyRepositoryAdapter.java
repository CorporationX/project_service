package faang.school.projectservice.repository.adapter.vacancy;

import faang.school.projectservice.exception.BusinessValidationException;
import faang.school.projectservice.exception.VacancyNotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class VacancyRepositoryAdapter {
    private final VacancyRepository vacancyRepository;

    public Vacancy getVacancyOrThrow(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException(id));
        if (Objects.equals(vacancy.getStatus(), VacancyStatus.CLOSED)) {
            throw new BusinessValidationException("Vacancy already closed");
        }
        return vacancy;
    }
}
