package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    @Query(
            "SELECT v FROM Vacancy v " +
            "WHERE v.name LIKE :namePattern " +
            "AND v.position = :positionPattern"
    )
    List<Vacancy> findAllByFilters(@Param("namePattern") String namePattern,
                                   @Param("positionPattern") TeamRole positionPattern);
}
