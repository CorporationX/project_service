package faang.school.projectservice.repository;

import faang.school.projectservice.model.ProjectReport;
import feign.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProjectReportRepository extends CrudRepository<ProjectReport, Long> {

    Optional<ProjectReport> getReportByProjectId(Long projectId);

    @Modifying
    void deleteReportByProjectId(@Param("projectId") Long projectId);
}
