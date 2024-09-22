package faang.school.projectservice.dto;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


public record MomentDto (
    Long id,
    String name,
    String description,
    LocalDateTime date,
    List<Long> projectIds,
    List<Long> userIds,
    String imageId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
