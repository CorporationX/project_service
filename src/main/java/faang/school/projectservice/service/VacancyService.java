package faang.school.projectservice.service;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository repository;

    public Vacancy createVacancy(Vacancy vacancy) {
            repository.save(vacancy);
        return vacancy;
    }

    public Vacancy updateVacancy(Vacancy vacancy) {
        if (vacancy.getStatus() == VacancyStatus.CLOSED) {
            if (vacancy.getCandidates() == null || vacancy.getCandidates().size() < vacancy.getCount()) {
                throw new IllegalStateException("Not enough candidates for closing vacancy.");
            }
        }
        return repository.save(vacancy);
    }


    public void deleteVacancy(Long vacancyId) {
        Vacancy vacancy = getVacancyById(vacancyId);
        List<Candidate> candidates = vacancy.getCandidates();
        if (candidates != null && !candidates.isEmpty()) {
            candidates.forEach(candidate -> candidate.setVacancy(null));
        }
        repository.deleteById(vacancyId);
    }

    public Vacancy getVacancyById(Long vacancyId) {
        return repository.findById(vacancyId)
                .orElseThrow(() -> new IllegalArgumentException("Vacancy not found"));
    }

    public List<Vacancy> filterVacancies(String position, String name) {
        return repository.findAll()
                .stream()
                .filter(vacancy -> vacancy
                        .getPosition()
                        .name()
                        .equals(position)
                        && vacancy.getName().equals(name))
                .collect(Collectors.toList());
    }

}
