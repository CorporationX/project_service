package faang.school.projectservice.dto.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Page {
    private int page = 0;
    private int pageSize = 10;
}
