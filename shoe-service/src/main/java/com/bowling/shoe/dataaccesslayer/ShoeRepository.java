package com.bowling.shoe.dataaccesslayer;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ShoeRepository extends JpaRepository<Shoe, ShoeIdentifier> {

}