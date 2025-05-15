package com.neogeo.tracking.repository;


import com.neogeo.tracking.model.Surveyor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyorRepository extends JpaRepository<Surveyor, String> {
    List<Surveyor> findByCity(String city);
    List<Surveyor> findByProjectName(String projectName);
    List<Surveyor> findByCityAndProjectName(String city, String projectName);
}
