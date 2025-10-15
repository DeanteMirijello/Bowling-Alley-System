package com.bowling.lane.presentationlayer;

import com.bowling.lane.dataaccesslayer.LaneStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaneRequestDTO {

    @NotNull
    private Integer laneNumber;

    @NotBlank
    private String zone;

    @NotNull
    private LaneStatus status;
}

