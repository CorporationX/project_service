package faang.school.projectservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ImageConfig {
    protected int fileSizeMb;
    protected int squareSize;
    protected int rectWidthSize;
    protected int rectHeightSize;
}
