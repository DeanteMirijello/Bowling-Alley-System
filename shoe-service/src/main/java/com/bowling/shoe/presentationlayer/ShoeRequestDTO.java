package com.bowling.shoe.presentationlayer;

import com.bowling.shoe.dataaccesslayer.ShoeSize;
import com.bowling.shoe.dataaccesslayer.ShoeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoeRequestDTO {

    @NotNull
    private ShoeSize size;

    @NotNull
    private LocalDate purchaseDate;

    @NotNull
    private ShoeStatus status;
}
