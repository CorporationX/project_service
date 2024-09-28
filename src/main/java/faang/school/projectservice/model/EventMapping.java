package faang.school.projectservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "event_mapping")
public class EventMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_mapping_seq")
    @SequenceGenerator(name = "event_mapping_seq", sequenceName = "event_mapping_seq", allocationSize = 1)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "google_event_id", nullable = false)
    private String googleEventId;
}
