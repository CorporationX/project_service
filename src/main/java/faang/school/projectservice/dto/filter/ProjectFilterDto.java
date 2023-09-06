package faang.school.projectservice.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFilterDto {
    private String namePattern;
    private String descriptionPattern;
    private Long ownerIdPattern;
    private String statusPattern;
    private String visibilityPattern;
}
