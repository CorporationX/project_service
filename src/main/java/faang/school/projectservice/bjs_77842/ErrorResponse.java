package faang.school.projectservice.bjs_77842;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private LocalDateTime localDateTime;
    private String message;

}