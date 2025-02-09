package faang.school.projectservice.repository;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface StageRepository extends JpaRepository<Stage, Long> {

    Stream<Stage> findAllByProjectId(Long projectId);
}
