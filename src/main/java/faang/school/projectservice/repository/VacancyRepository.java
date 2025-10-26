package faang.school.projectservice.repository;

import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    default Vacancy getByIdOrThrow(long Id) {
        return findById(Id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vacancy %d not found", Id))
        );
    }

    default Vacancy getWithCandidatesOrThrow(long Id) {
        return getWithCandidates(Id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vacancy %d not found", Id))
        );
    }

    @Query("SELECT v FROM Vacancy v LEFT JOIN FETCH v.candidates WHERE v.id = :id")
    Optional<Vacancy> getWithCandidates(@Param("id") Long Id);

    @Query("SELECT v FROM Vacancy v")
    List<Vacancy> findAllVacancies();

    @Query("""
            SELECT v FROM Vacancy v WHERE
            (:description IS NULL OR LOWER(v.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND
            (:position IS NULL OR v.position = :position)
            """)
    List<Vacancy> findVacancyByFilters(@Param("description") String description,
                                       @Param("position") TeamRole position);

    @Query("""
            SELECT v FROM Vacancy v WHERE
            (:description IS NULL OR LOWER(v.description) LIKE LOWER(CONCAT('%', :description, '%')))
            """)
    List<Vacancy> findVacancyByDescription(@Param("description") String description);

    @Query("""
            SELECT v FROM Vacancy v WHERE
            (:position IS NULL OR v.position = :position)
            """)
    List<Vacancy> findVacancyByPosition(@Param("position") TeamRole position);
}
