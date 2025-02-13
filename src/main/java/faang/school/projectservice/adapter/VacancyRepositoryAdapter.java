package faang.school.projectservice.adapter;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyRepositoryAdapter {
    private final VacancyRepository vacancyRepository;

    public Vacancy findById(Long id) {
        return vacancyRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy with id " + id + " was not found"));
    }

    public void save(Vacancy vacancy) {
        vacancyRepository.save(vacancy);
    }
}