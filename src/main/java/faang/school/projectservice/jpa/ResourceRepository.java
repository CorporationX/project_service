package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByIdAndProjectId(long id, long projectId);

    Optional<Resource> findResourceByIdAndProjectIdAndStatusNot(long id, long projectId, ResourceStatus statusNot);

    List<Resource> findAllByProjectId(long projectId);
}
