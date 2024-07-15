package faang.school.projectservice.repository;

import faang.school.projectservice.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    Vacancy save(Vacancy vacancy);

    Optional<Vacancy> findById(long id);

    List<Vacancy> findAll();

    void deleteById(long id);
}
