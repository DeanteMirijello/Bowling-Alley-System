package com.bowling.shoe.dataaccesslayer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "shoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Shoe {

    @EmbeddedId
    private ShoeIdentifier id;

    @Enumerated(EnumType.STRING)
    private ShoeSize size;

    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    private ShoeStatus status;
}
