package com.bowling.lane.dataaccesslayer;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lanes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lane {

    @EmbeddedId
    private LaneIdentifier id;

    private Integer laneNumber;

    @Embedded
    private LaneZone zone;

    @Enumerated(EnumType.STRING)
    private LaneStatus status;
}

