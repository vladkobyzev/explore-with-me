package ru.practicum.models;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.events.Location;
import ru.practicum.util.EventStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation")
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @JoinColumn(name = "confirmed_requests")
    private int confirmedRequests;
    @JoinColumn(name = "created_on")
    private LocalDateTime createdOn;
    @JoinColumn(name = "description")
    private String description;
    @JoinColumn(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventStatus state = EventStatus.PENDING;
    @Column(name = "title")
    private String title;
    @Column(name = "views")
    private Integer views;

    public void incrementConfirmedRequests() {
        this.confirmedRequests++;
    }
}

