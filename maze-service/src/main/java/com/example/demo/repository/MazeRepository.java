package com.example.demo.repository;

import com.example.demo.models.entities.MazeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MazeRepository extends MongoRepository<MazeEntity, String> {
}
