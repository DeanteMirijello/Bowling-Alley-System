package com.bowling.lane.dataaccesslayer;

import com.bowling.lane.mappinglayer.LaneMapper;
import com.bowling.lane.mappinglayer.LaneMapperImpl;
import com.bowling.lane.presentationlayer.LaneResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
class LaneRepositoryIntegrationTest {

    @Autowired
    private LaneRepository laneRepository;

    @BeforeEach
    void setUpDB() {
        laneRepository.deleteAll();
    }

    @Test
    public void whenLanesExist_thenReturnAllLanes() {
        Lane lane1 = new Lane(LaneIdentifier.generate(), 1, new LaneZone("A"), LaneStatus.AVAILABLE);
        Lane lane2 = new Lane(LaneIdentifier.generate(), 2, new LaneZone("B"), LaneStatus.IN_USE);
        laneRepository.save(lane1);
        laneRepository.save(lane2);

        List<Lane> lanes = laneRepository.findAll();

        assertNotNull(lanes);
        assertEquals(2, lanes.size());
    }

    @Test
    public void whenLaneExists_thenReturnById() {
        LaneIdentifier id = LaneIdentifier.generate();
        Lane lane = new Lane(id, 3, new LaneZone("C"), LaneStatus.MAINTENANCE);
        laneRepository.save(lane);

        Optional<Lane> found = laneRepository.findById(id);

        assertTrue(found.isPresent());
        assertEquals(lane.getLaneNumber(), found.get().getLaneNumber());
    }

    @Test
    public void whenLaneDoesNotExist_thenReturnEmpty() {
        Optional<Lane> notFound = laneRepository.findById(LaneIdentifier.generate());

        assertTrue(notFound.isEmpty());
    }

    @Test
    public void whenLaneIsUpdated_thenReturnUpdatedLane() {
        Lane lane = new Lane(LaneIdentifier.generate(), 4, new LaneZone("D"), LaneStatus.AVAILABLE);
        laneRepository.save(lane);

        lane.setStatus(LaneStatus.MAINTENANCE);
        laneRepository.save(lane);

        Lane updated = laneRepository.findById(lane.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(LaneStatus.MAINTENANCE, updated.getStatus());
    }

    @Test
    public void whenLaneIsDeleted_thenItShouldNotBeFound() {
        Lane lane = new Lane(LaneIdentifier.generate(), 5, new LaneZone("E"), LaneStatus.AVAILABLE);
        laneRepository.save(lane);
        laneRepository.delete(lane);

        Optional<Lane> deleted = laneRepository.findById(lane.getId());

        assertTrue(deleted.isEmpty());
    }

    @Test
    public void printAllLanesFromScript() {
        List<Lane> lanes = laneRepository.findAll();
        System.out.println("LANES FROM DB: " + lanes.size());
        lanes.forEach(lane -> System.out.println(lane.getId().getId() + " | " + lane.getLaneNumber()));
    }

    @Test
    void whenMappingLaneWithNullId_thenResponseDTOHasNullId() {
        LaneMapper mapper = new LaneMapperImpl();
        Lane lane = Lane.builder()
                .id(null)
                .laneNumber(6)
                .zone(new LaneZone("F"))
                .status(LaneStatus.AVAILABLE)
                .build();

        LaneResponseDTO dto = mapper.toResponseDTO(lane);
        assertThat(dto.getId()).isNull();
    }

    @Test
    void whenMappingLaneWithValidId_thenResponseDTOHasSameId() {
        String uuid = UUID.randomUUID().toString();
        LaneMapper mapper = new LaneMapperImpl();
        Lane lane = Lane.builder()
                .id(new LaneIdentifier(uuid))
                .laneNumber(7)
                .zone(new LaneZone("G"))
                .status(LaneStatus.IN_USE)
                .build();

        LaneResponseDTO dto = mapper.toResponseDTO(lane);
        assertThat(dto.getId()).isEqualTo(uuid);
    }

    @Test
    void whenComparingLaneZones_thenEqualsAndHashCodeWork() {
        LaneZone zone1 = new LaneZone("H");
        LaneZone zone2 = new LaneZone("H");
        LaneZone zone3 = new LaneZone("I");

        assertThat(zone1).isEqualTo(zone2);
        assertThat(zone1.hashCode()).isEqualTo(zone2.hashCode());
        assertThat(zone1).isNotEqualTo(zone3);
        assertThat(zone1).isNotEqualTo(null);
        assertThat(zone1).isNotEqualTo("H");
        assertThat(zone1).isEqualTo(zone1);
    }

    @Test
    void whenComparingLaneIdentifiers_thenAllEqualsBranchesAreCovered() {
        String uuid = UUID.randomUUID().toString();
        LaneIdentifier id1 = new LaneIdentifier(uuid);
        LaneIdentifier id2 = new LaneIdentifier(uuid);
        LaneIdentifier id3 = new LaneIdentifier(UUID.randomUUID().toString());

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        assertThat(id1).isNotEqualTo(id3);
        assertThat(id1).isNotEqualTo(null);
        assertThat(id1).isNotEqualTo("not-an-id");
        assertThat(id1).isEqualTo(id1);
    }
}
