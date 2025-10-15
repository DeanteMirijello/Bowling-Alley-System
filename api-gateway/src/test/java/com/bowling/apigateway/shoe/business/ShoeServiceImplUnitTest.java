package com.bowling.apigateway.shoe.business;

import com.bowling.apigateway.shoe.domainclient.ShoeClient;
import com.bowling.apigateway.shoe.presentation.ShoeRequestDTO;
import com.bowling.apigateway.shoe.presentation.ShoeResponseDTO;
import com.bowling.apigateway.shoe.presentation.ShoeSize;
import com.bowling.apigateway.shoe.presentation.ShoeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShoeServiceImplUnitTest {

    @Mock
    private ShoeClient shoeClient;

    @InjectMocks
    private ShoeServiceImpl shoeService;

    private ShoeRequestDTO request;
    private ShoeResponseDTO response;
    private String validId;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        request = ShoeRequestDTO.builder()
                .size(ShoeSize.SIZE_9)
                .purchaseDate(LocalDate.now().minusDays(30))
                .status(ShoeStatus.AVAILABLE)
                .build();

        response = ShoeResponseDTO.builder()
                .id(validId)
                .size(ShoeSize.SIZE_9)
                .purchaseDate(request.getPurchaseDate())
                .status(ShoeStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenCreate_thenReturnsEntityModel() {
        Mockito.when(shoeClient.create(request)).thenReturn(response);

        EntityModel<ShoeResponseDTO> result = shoeService.create(request);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenGetById_thenReturnsEntityModel() {
        Mockito.when(shoeClient.get(validId)).thenReturn(response);

        EntityModel<ShoeResponseDTO> result = shoeService.getById(validId);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenGetAll_thenReturnsCollectionModel() {
        List<ShoeResponseDTO> mockList = List.of(response);
        Mockito.when(shoeClient.getAll()).thenReturn(mockList);

        CollectionModel<EntityModel<ShoeResponseDTO>> result = shoeService.getAll();

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().stream().findFirst().get().getContent()).isEqualTo(response);
    }

    @Test
    void whenUpdate_thenReturnsEntityModel() {
        Mockito.when(shoeClient.update(validId, request)).thenReturn(response);

        EntityModel<ShoeResponseDTO> result = shoeService.update(validId, request);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenDelete_thenDelegatesToClient() {
        shoeService.delete(validId);

        Mockito.verify(shoeClient).delete(validId);
    }
}
