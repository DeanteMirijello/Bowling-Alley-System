package com.bowling.apigateway.bowlingball.business;

import com.bowling.apigateway.bowlingball.domainclient.BowlingBallClient;
import com.bowling.apigateway.bowlingball.presentation.BallSize;
import com.bowling.apigateway.bowlingball.presentation.BallStatus;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallRequestDTO;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallResponseDTO;
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
class BowlingBallServiceImplUnitTest {

    @Mock
    private BowlingBallClient bowlingBallClient;

    @InjectMocks
    private BowlingBallServiceImpl bowlingBallService;

    private BowlingBallRequestDTO request;
    private BowlingBallResponseDTO response;
    private String validId;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        request = BowlingBallRequestDTO.builder()
                .size(BallSize.TEN)
                .gripType("FINGER")
                .color("Red")
                .status(BallStatus.AVAILABLE)
                .build();

        response = BowlingBallResponseDTO.builder()
                .id(validId)
                .size(BallSize.TEN)
                .gripType("FINGER")
                .color("Red")
                .status(BallStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenCreate_thenReturnsEntityModel() {
        Mockito.when(bowlingBallClient.createBall(request)).thenReturn(response);

        EntityModel<BowlingBallResponseDTO> result = bowlingBallService.create(request);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenGetById_thenReturnsEntityModel() {
        Mockito.when(bowlingBallClient.getBall(validId)).thenReturn(response);

        EntityModel<BowlingBallResponseDTO> result = bowlingBallService.getById(validId);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenGetAll_thenReturnsCollectionModel() {
        List<BowlingBallResponseDTO> mockList = List.of(response);

        Mockito.when(bowlingBallClient.getAll()).thenReturn(mockList);

        CollectionModel<EntityModel<BowlingBallResponseDTO>> result = bowlingBallService.getAll();

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().stream().findFirst().get().getContent()).isEqualTo(response);
    }

    @Test
    void whenUpdate_thenReturnsEntityModel() {
        Mockito.when(bowlingBallClient.updateBall(validId, request)).thenReturn(response);

        EntityModel<BowlingBallResponseDTO> result = bowlingBallService.update(validId, request);

        assertThat(result.getContent()).isEqualTo(response);
    }

    @Test
    void whenDelete_thenDelegatesToClient() {
        bowlingBallService.delete(validId);

        Mockito.verify(bowlingBallClient).deleteBall(validId);
    }
}
