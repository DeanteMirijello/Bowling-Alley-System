package com.bowling.apigateway.lane.presentation;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaneResponseDTO {

    private String id;
    private Integer laneNumber;
    private String zone;
    private LaneStatus status;
}

