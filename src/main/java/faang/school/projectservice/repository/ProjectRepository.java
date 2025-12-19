package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END 
            FROM Project p 
            WHERE p.ownerId = :ownerId AND p.name = :name
            """
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Modifying
    @Query("UPDATE Project p SET p.storageSize = :size WHERE p.id = :id")
    int updateStorageSize(
            @Param("id") Long id,
            @Param("size") Long size
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Project p WHERE p.id = :id")
    Optional<Project> findByIdWithLock(@Param("id") Long id);
}

