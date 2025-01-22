package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyRepositoryAdapter {
    private final VacancyRepository vacancyRepository;

    public Vacancy getById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found with id: " + id));
    }
}
