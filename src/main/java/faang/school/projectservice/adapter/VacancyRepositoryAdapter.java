package faang.school.projectservice.adapter;

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
                .orElseThrow(() -> new IllegalArgumentException(String.format("Vacancy with id: %d not found!", id)));
    }

    public void save(Vacancy vacancy) {
        vacancyRepository.save(vacancy);
    }
}
