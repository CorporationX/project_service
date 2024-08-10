package faang.school.projectservice.jpa;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageJpaRepository extends JpaRepository<Stage, Long> {

    @Query("SELECT s FROM Stage s WHERE s.project.status = :status")
    List<Stage> findByStatus(ProjectStatus status);
}
