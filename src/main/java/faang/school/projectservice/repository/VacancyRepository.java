package faang.school.projectservice.repository;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    Vacancy save(Vacancy vacancy);

    Optional<Vacancy> findById(long id);

    List<Vacancy> findAll();

    void deleteById(long id);


    @Query("SELECT v FROM Vacancy v WHERE " +
            "(:name IS NULL OR v.name LIKE %:name%) AND " +
            "(:status IS NULL OR v.status = :status)")
    List<Vacancy> findByFilter(@Param("name") String name, @Param("status") VacancyStatus status);
}
