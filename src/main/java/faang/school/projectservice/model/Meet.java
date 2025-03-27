package faang.school.projectservice.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "meet")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
public class Meet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", length = 128, nullable = false)
    private String title;

    @Column(name = "description", length = 512, nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetStatus status;

    @Column(name = "creator_id", nullable = false)
    private long creatorId;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ElementCollection
    @CollectionTable(name = "meet_participant", joinColumns = @JoinColumn(name = "meet_id"))
    @Column(name = "user_id")
    private List<Long> userIds;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
