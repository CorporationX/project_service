package faang.school.projectservice.valitator.initiative;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class InitiativeStatusDoneValidator {

    public void checkCompletedStages(Initiative initiative){
        for(Stage stage: initiative.getStages()){
            for(Task task: stage.getTasks()){
                if(!task.getStatus().equals(TaskStatus.DONE)){
                    throw new RuntimeException("Unable to complete there are uncompleted stages!");
                }
            }
        }
    }
}
