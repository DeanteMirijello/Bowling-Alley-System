package com.bowling.transaction.domainclientlayer.lane;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaneModel {

    private String id;

    private Integer laneNumber;

    private String zone;

    private LaneStatus status;
}

