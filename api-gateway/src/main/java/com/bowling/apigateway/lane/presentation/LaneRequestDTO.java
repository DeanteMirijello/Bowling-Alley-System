package com.bowling.apigateway.lane.presentation;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaneRequestDTO {

    private Integer laneNumber;
    private String zone;
    private LaneStatus status;
}

