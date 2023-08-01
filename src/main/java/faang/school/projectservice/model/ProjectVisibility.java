package faang.school.projectservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectVisibility {
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");
    private String projectVisibility;

}
