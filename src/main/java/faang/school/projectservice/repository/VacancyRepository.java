package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    @Query(
            "SELECT v FROM Vacancy v " +
                    "WHERE (:position IS NULL OR v.position = :position) " +
                    "AND (:filter IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :filter, '%')))"
    )
    Page<Vacancy> findAllByFilter(String filter, TeamRole position, Pageable pageable);
}
