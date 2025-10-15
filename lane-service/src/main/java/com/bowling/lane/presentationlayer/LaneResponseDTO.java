package com.bowling.lane.presentationlayer;

import com.bowling.lane.dataaccesslayer.LaneStatus;
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

