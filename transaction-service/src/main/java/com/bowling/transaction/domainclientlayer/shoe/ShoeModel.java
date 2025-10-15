package com.bowling.transaction.domainclientlayer.shoe;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoeModel {

    private String id;

    private ShoeSize size;

    private LocalDate purchaseDate;

    private ShoeStatus status;
}

