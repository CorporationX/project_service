package faang.school.projectservice.repository;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    @Query(nativeQuery = true,value = """
    INSERT INTO vacancy (name,description,project_id,created_at,updated_at, created_by,updated_by, status ,salary, work_schedule, count)
    VALUES (?1,?2,?3,NOW(),NOW(),?4,?5,?6,?7,?8,?9 )
    RETURNING *;
    """)
     Vacancy create(String name, String description,Long projectId,Long createdBy, Long updatedBy, String status,
                    Double salary, String workSchedule,
                    Integer count);
}
