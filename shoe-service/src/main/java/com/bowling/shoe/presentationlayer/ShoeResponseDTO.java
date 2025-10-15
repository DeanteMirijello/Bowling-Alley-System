package com.bowling.shoe.presentationlayer;


import com.bowling.shoe.dataaccesslayer.ShoeSize;
import com.bowling.shoe.dataaccesslayer.ShoeStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoeResponseDTO {

    private String id;
    private ShoeSize size;
    private LocalDate purchaseDate;
    private ShoeStatus status;
}
