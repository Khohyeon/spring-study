package com.example.cassandra.repository;

import com.example.cassandra.entity.Vet;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface VetRepository extends CrudRepository<Vet, UUID> {
    Vet findByFirstName(String firstName);
}
