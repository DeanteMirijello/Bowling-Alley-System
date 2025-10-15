package com.bowling.apigateway.lane.business;

import com.bowling.apigateway.lane.domainclient.LaneClient;
import com.bowling.apigateway.lane.presentation.LaneRequestDTO;
import com.bowling.apigateway.lane.presentation.LaneResponseDTO;
import com.bowling.apigateway.lane.presentation.LaneStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LaneServiceImplUnitTest {

    @Mock
    private LaneClient laneClient;

    @InjectMocks
    private LaneServiceImpl laneService;

    private LaneRequestDTO request;
    private LaneResponseDTO response;
    private String validId;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        request = LaneRequestDTO.builder()
                .laneNumber(4)
                .zone("A")
                .status(LaneStatus.AVAILABLE)
                .build();

        response = LaneResponseDTO.builder()
                .id(validId)
                .laneNumber(4)
                .zone("A")
                .status(LaneStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenCreate_thenReturnsEntityModel() {
        Mockito.when(laneClient.create(request)).thenReturn(response);

        EntityModel<LaneResponseDTO> result = laneService.create(request);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenGetById_thenReturnsEntityModel() {
        Mockito.when(laneClient.get(validId)).thenReturn(response);

        EntityModel<LaneResponseDTO> result = laneService.getById(validId);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenGetAll_thenReturnsCollectionModel() {
        Mockito.when(laneClient.getAll()).thenReturn(List.of(response));

        CollectionModel<EntityModel<LaneResponseDTO>> result = laneService.getAll();

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().stream().findFirst().get().getContent()).isEqualTo(response);
    }

    @Test
    void whenUpdate_thenReturnsEntityModel() {
        Mockito.when(laneClient.update(validId, request)).thenReturn(response);

        EntityModel<LaneResponseDTO> result = laneService.update(validId, request);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenDelete_thenDelegatesToClient() {
        laneService.delete(validId);

        Mockito.verify(laneClient).delete(validId);
    }
}
