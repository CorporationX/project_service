package faang.school.projectservice.dto.client;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ProjectDto — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 14.08.2025
 */
@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String visibility;
    private Long ownerId;
    private LocalDateTime updatedAt;
}