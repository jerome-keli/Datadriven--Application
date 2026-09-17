package com.example.pharmastock.repository;

import com.example.pharmastock.model.Drug;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DrugRepository extends MongoRepository<Drug, String> {

    // Uses the text index on name + genericName (see Drug model)
    @Query("{ '$text': { '$search': ?0 } }")
    List<Drug> searchByText(String keyword);

    List<Drug> findByCategoryIgnoreCase(String category);
}
