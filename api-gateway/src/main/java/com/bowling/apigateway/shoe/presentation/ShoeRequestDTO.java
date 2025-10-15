package com.bowling.apigateway.shoe.presentation;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoeRequestDTO {

    private ShoeSize size;
    private LocalDate purchaseDate;
    private ShoeStatus status;
}

