package faang.school.projectservice.controller.stage.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StageErrorResponse {
    private String message;
    private long timestamp;
}
