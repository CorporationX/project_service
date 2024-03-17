package faang.school.projectservice.service;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TaskPerformanceTest extends TestSetUp{
    @InjectMocks
    private TaskPerformance taskPerformance;


    @Test
    @DisplayName("Grouping all interns' ids by task assigned to them")
    void testMapPerformerUserIdsAndTasks(){
        Assert.assertEquals(map, taskPerformance.mapPerformersUserIdsAndTasks(firstInternship));
    }

    @Test
    @DisplayName("testTaskPerformance")
    void testPartitionByStatusDone(){
        Assert.assertEquals(map1, taskPerformance.partitionByStatusDone(firstInternship));
    }
}