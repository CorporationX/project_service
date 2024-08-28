package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {

    private Integer page = 0;
    private Integer size = 10;
    private String sortBy;
    private String sortDirection = "desc";
}
