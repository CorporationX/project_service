package faang.school.projectservice.repository;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface StageRepository extends JpaRepository<Stage, Long> {
    @Query("FROM Stage " +
            "WHERE project.id = :projectId")
    Stream<Stage> findAllByProjectId(Long projectId);
}
