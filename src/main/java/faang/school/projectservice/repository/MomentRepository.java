package faang.school.projectservice.repository;

import faang.school.projectservice.model.Moment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MomentRepository extends JpaRepository<Moment, Long> {
    @Query(nativeQuery = true, value = """
            SELECT m.* FROM moment m
            WHERE m.date BETWEEN :startDate AND :endDate
            """)
    List<Moment> findAllByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query(nativeQuery = true, value = """
            SELECT m.* FROM moment m
            JOIN moment_project mp
            ON m.id = mp.moment_id
            WHERE mp.project_id IN (:projectIds)
            """)
    List<Moment> findAllByProjectIds(List<Long> projectIds);
}
