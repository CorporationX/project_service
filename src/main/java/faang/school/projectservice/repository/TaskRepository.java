package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository {

    @Query("select t from Task t where t.id in :taskIds")
    List<Task> findAllById(List<Long> taskIds);
}
