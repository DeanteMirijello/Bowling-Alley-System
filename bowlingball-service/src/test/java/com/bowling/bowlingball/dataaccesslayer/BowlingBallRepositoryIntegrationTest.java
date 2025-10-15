package com.bowling.bowlingball.dataaccesslayer;

import com.bowling.bowlingball.mappinglayer.BowlingBallMapperImpl;
import com.bowling.bowlingball.presentationlayer.BowlingBallResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class BowlingBallRepositoryIntegrationTest {

    @Autowired
    private BowlingBallRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    public void whenBowlingBallsExist_thenReturnAll() {
        BowlingBall b1 = new BowlingBall(BowlingBallIdentifier.generate(), BallSize.TEN, "Standard", "Blue", BallStatus.AVAILABLE);
        BowlingBall b2 = new BowlingBall(BowlingBallIdentifier.generate(), BallSize.SIX, "Contoured", "Red", BallStatus.IN_USE);

        repository.save(b1);
        repository.save(b2);

        List<BowlingBall> allBalls = repository.findAll();

        assertNotNull(allBalls);
        assertEquals(2, allBalls.size());
    }

    @Test
    public void whenBowlingBallExists_thenReturnById() {
        BowlingBallIdentifier id = BowlingBallIdentifier.generate();
        BowlingBall ball = new BowlingBall(id, BallSize.TWELVE, "Textured", "Black", BallStatus.AVAILABLE);

        repository.save(ball);

        Optional<BowlingBall> found = repository.findById(id);

        assertTrue(found.isPresent());
        assertEquals(BallSize.TWELVE, found.get().getSize());
    }

    @Test
    public void whenBowlingBallDoesNotExist_thenReturnEmpty() {
        Optional<BowlingBall> result = repository.findById(BowlingBallIdentifier.generate());
        assertTrue(result.isEmpty());
    }

    @Test
    public void whenBowlingBallIsUpdated_thenReturnUpdated() {
        BowlingBall ball = new BowlingBall(BowlingBallIdentifier.generate(), BallSize.EIGHT, "Basic", "Green", BallStatus.AVAILABLE);
        repository.save(ball);

        ball.setColor("Purple");
        ball.setStatus(BallStatus.IN_USE);
        repository.save(ball);

        Optional<BowlingBall> updated = repository.findById(ball.getId());
        assertTrue(updated.isPresent());
        assertEquals("Purple", updated.get().getColor());
        assertEquals(BallStatus.IN_USE, updated.get().getStatus());
    }

    @Test
    public void whenBowlingBallIsDeleted_thenShouldNotExist() {
        BowlingBall ball = new BowlingBall(BowlingBallIdentifier.generate(), BallSize.SIXTEEN, "Standard", "Black", BallStatus.AVAILABLE);
        repository.save(ball);

        repository.delete(ball);

        Optional<BowlingBall> deleted = repository.findById(ball.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void whenMappingEntityWithNullId_thenResponseDTOHasNullId() {
        BowlingBall ball = BowlingBall.builder()
                .id(null)
                .size(BallSize.SIX)
                .gripType("Hybrid")
                .color("Gray")
                .status(BallStatus.AVAILABLE)
                .build();

        BowlingBallResponseDTO dto = new BowlingBallMapperImpl().toResponseDTO(ball);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
    }

    @Test
    void whenMappingEntityWithValidId_thenResponseDTOHasSameId() {
        String idStr = UUID.randomUUID().toString();
        BowlingBall ball = BowlingBall.builder()
                .id(new BowlingBallIdentifier(idStr))
                .size(BallSize.TWELVE)
                .gripType("Hybrid")
                .color("Yellow")
                .status(BallStatus.IN_USE)
                .build();

        BowlingBallResponseDTO dto = new BowlingBallMapperImpl().toResponseDTO(ball);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(idStr);
    }

    @Test
    void whenComparingIdentifiers_thenEqualsAndHashCodeWork() {
        String uuid = UUID.randomUUID().toString();

        BowlingBallIdentifier id1 = new BowlingBallIdentifier(uuid);
        BowlingBallIdentifier id2 = new BowlingBallIdentifier(uuid);
        BowlingBallIdentifier id3 = new BowlingBallIdentifier(UUID.randomUUID().toString());

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        assertThat(id1).isNotEqualTo(id3);
        assertThat(id1).isNotEqualTo(null);
        assertThat(id1).isNotEqualTo("some-string");
        assertThat(id1).isEqualTo(id1);
    }
}
