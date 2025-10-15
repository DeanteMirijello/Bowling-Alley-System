package com.bowling.shoe.dataaccesslayer;

import com.bowling.shoe.mappinglayer.ShoeMapper;
import com.bowling.shoe.mappinglayer.ShoeMapperImpl;
import com.bowling.shoe.presentationlayer.ShoeResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ShoeRepositoryIntegrationTest {

    @Autowired
    private ShoeRepository shoeRepository;

    @BeforeEach
    void setup() {
        shoeRepository.deleteAll();
    }

    @Test
    void whenShoeSaved_thenCanBeFoundById() {
        ShoeIdentifier id = ShoeIdentifier.generate();
        Shoe shoe = new Shoe(id, ShoeSize.SIZE_7, LocalDate.now(), ShoeStatus.IN_USE);
        shoeRepository.save(shoe);

        Optional<Shoe> found = shoeRepository.findById(id);

        assertTrue(found.isPresent());
        assertEquals(ShoeSize.SIZE_7, found.get().getSize());
    }

    @Test
    void whenShoesExist_thenReturnAllShoes() {
        Shoe s1 = new Shoe(ShoeIdentifier.generate(), ShoeSize.SIZE_9, LocalDate.now(), ShoeStatus.AVAILABLE);
        Shoe s2 = new Shoe(ShoeIdentifier.generate(), ShoeSize.SIZE_10, LocalDate.now(), ShoeStatus.IN_USE);
        shoeRepository.save(s1);
        shoeRepository.save(s2);

        List<Shoe> result = shoeRepository.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void whenShoeIsUpdated_thenChangesAreSaved() {
        Shoe shoe = new Shoe(ShoeIdentifier.generate(), ShoeSize.SIZE_6, LocalDate.now(), ShoeStatus.AVAILABLE);
        shoeRepository.save(shoe);

        shoe.setStatus(ShoeStatus.IN_USE);
        shoeRepository.save(shoe);

        Shoe updated = shoeRepository.findById(shoe.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(ShoeStatus.IN_USE, updated.getStatus());
    }

    @Test
    void whenShoeDeleted_thenItIsGone() {
        Shoe shoe = new Shoe(ShoeIdentifier.generate(), ShoeSize.SIZE_8, LocalDate.now(), ShoeStatus.IN_USE);
        shoeRepository.save(shoe);
        shoeRepository.delete(shoe);

        Optional<Shoe> result = shoeRepository.findById(shoe.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void whenShoeNotFound_thenReturnEmpty() {
        Optional<Shoe> notFound = shoeRepository.findById(ShoeIdentifier.generate());
        assertTrue(notFound.isEmpty());
    }

    @Test
    void whenMappingShoeWithNullId_thenResponseDTOHasNullId() {
        ShoeMapper mapper = new ShoeMapperImpl();
        Shoe shoe = new Shoe(null, ShoeSize.SIZE_8, LocalDate.now(), ShoeStatus.AVAILABLE);

        ShoeResponseDTO dto = mapper.toResponseDTO(shoe);
        assertThat(dto.getId()).isNull();
    }

    @Test
    void whenMappingShoeWithValidId_thenResponseDTOHasId() {
        UUID uuid = UUID.randomUUID();
        ShoeMapper mapper = new ShoeMapperImpl();
        Shoe shoe = new Shoe(new ShoeIdentifier(uuid), ShoeSize.SIZE_10, LocalDate.now(), ShoeStatus.IN_USE);

        ShoeResponseDTO dto = mapper.toResponseDTO(shoe);
        assertThat(dto.getId()).isEqualTo(uuid.toString());
    }

    @Test
    void whenMappingEntityWithNullId_thenResponseDTOHasNullId() {
        ShoeMapper mapper = new ShoeMapperImpl();
        Shoe shoe = new Shoe(null, ShoeSize.SIZE_10, LocalDate.now(), ShoeStatus.AVAILABLE);

        ShoeResponseDTO dto = mapper.toResponseDTO(shoe);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
    }

    @Test
    void whenMappingEntityWithValidId_thenResponseDTOHasSameId() {
        UUID id = UUID.randomUUID();
        ShoeMapper mapper = new ShoeMapperImpl();
        Shoe shoe = new Shoe(new ShoeIdentifier(id), ShoeSize.SIZE_9, LocalDate.now(), ShoeStatus.IN_USE);

        ShoeResponseDTO dto = mapper.toResponseDTO(shoe);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id.toString());
    }

    @Test
    void whenComparingShoeIdentifiers_thenEqualsAndHashCodeBehaveCorrectly() {
        UUID id = UUID.randomUUID();

        ShoeIdentifier id1 = new ShoeIdentifier(id);
        ShoeIdentifier id2 = new ShoeIdentifier(id);
        ShoeIdentifier id3 = new ShoeIdentifier(UUID.randomUUID());

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        assertThat(id1).isNotEqualTo(id3);
        assertThat(id1).isNotEqualTo(null);
        assertThat(id1).isNotEqualTo("not-an-id");
        assertThat(id1).isEqualTo(id1);
    }
}
