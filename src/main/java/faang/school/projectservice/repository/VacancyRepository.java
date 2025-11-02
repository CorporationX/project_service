package faang.school.projectservice.repository;

import faang.school.projectservice.exception.vacancy.EntityNotFoundException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    default Vacancy getByIdOrThrow(long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vacancy %d not found", id))
        );
    }

    default Vacancy getWithCandidatesOrThrow(long id) {
        return getWithCandidates(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vacancy %d not found", id))
        );
    }

    @Query(""" 
            SELECT v FROM Vacancy v
            LEFT JOIN FETCH v.candidates
            WHERE v.id = :id
            """)
    Optional<Vacancy> getWithCandidates(@Param("id") Long id);


    @Query("""
            SELECT v FROM Vacancy v
            WHERE (:description IS NULL OR LOWER(v.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND
                  (:position IS NULL OR LOWER(v.position) LIKE LOWER(CONCAT('%', :position, '%')))
            """)
    List<Vacancy> findVacancyByFilters(@Param("description") String description,
                                       @Param("position") TeamRole position);

    @Query("""
            SELECT v FROM Vacancy v 
            WHERE (:description IS NULL OR v.description ILIKE CONCAT('%', :description, '%'))
            """)
    List<Vacancy> findVacancyByDescription(@Param("description") String description);

    @Query("""
            SELECT v FROM Vacancy v
            WHERE v.position = :position
            """)
    List<Vacancy> findVacancyByPosition(@Param("position") TeamRole position);
}
