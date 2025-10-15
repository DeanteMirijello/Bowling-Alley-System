package com.bowling.transaction.mappinglayer;

import com.bowling.transaction.dataaccesslayer.Transaction;
import com.bowling.transaction.presentationlayer.TransactionRequestDTO;
import com.bowling.transaction.presentationlayer.TransactionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "transactionIdentifier.id", target = "transactionId")
    TransactionResponseDTO toResponseDTO(Transaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionIdentifier", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "dateCompleted", ignore = true)
    Transaction toEntity(TransactionRequestDTO dto);
}




