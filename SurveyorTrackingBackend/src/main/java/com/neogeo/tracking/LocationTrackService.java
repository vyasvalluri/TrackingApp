package com.neogeo.tracking;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neogeo.tracking.model.LocationTrack;
import com.neogeo.tracking.model.Surveyor;
import com.neogeo.tracking.repository.LocationTrackRepository;
import com.neogeo.tracking.repository.SurveyorRepository;

@Service
public class LocationTrackService {

    @Autowired
    private LocationTrackRepository locationTrackRepository;

    @Autowired
    private SurveyorRepository surveyorRepository;

    // Get filtered surveyors
    public List<Surveyor> filterSurveyors(String city, String project, String status) {
        if (city != null && project != null) {
            return surveyorRepository.findByCityAndProjectName(city, project);
        } else if (city != null) {
            List<Surveyor> surveyors = surveyorRepository.findByCity(city);
            System.out.println("Surveyors in city " + city + ": " + surveyors);
            return surveyors;
        } else if (project != null) {
            return surveyorRepository.findByProjectName(project);
        } else {
            return surveyorRepository.findAll();
        }
    }

    // Get latest location
    public LocationTrack getLatestLocation(String surveyorId) {
        return locationTrackRepository
                .findTopBySurveyorIdOrderByTimestampDesc(surveyorId)
                .orElse(null);
    }

    // Get location history
    public List<LocationTrack> getTrackHistory(String surveyorId, LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return locationTrackRepository.findBySurveyorIdAndTimestampBetweenOrderByTimestampAsc(surveyorId, start, end);
        } else {
            return locationTrackRepository.findBySurveyorIdOrderByTimestampAsc(surveyorId);
        }
    }
}
