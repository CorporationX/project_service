package faang.school.projectservice.repository;

import faang.school.projectservice.model.meet.Meet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {

    @Query("SELECT m FROM Meet m WHERE m.project.id = :projectId")
    List<Meet> getByProjectId(Long projectId);
}
