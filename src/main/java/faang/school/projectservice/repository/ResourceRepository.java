package faang.school.projectservice.repository;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Modifying
    @Query("UPDATE Resource r SET r.STATUS = 'DELETED', r.key = null, r.size = 0, r.updatedBy = :updatedBy, " +
            "r.updatedAt = CURRENT_TIMESTAMP WHERE r.id = :id")
    void softDelete(@Param("id") Long id, @Param("updatedBy") TeamMember updatedBy);
}
