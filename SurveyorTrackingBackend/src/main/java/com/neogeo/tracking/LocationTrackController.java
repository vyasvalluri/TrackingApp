package com.neogeo.tracking;

import java.time.LocalDateTime;
import java.util.List;

import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neogeo.tracking.dto.LiveLocationMessage;
import com.neogeo.tracking.model.LocationTrack;
import com.neogeo.tracking.model.Surveyor;
import com.neogeo.tracking.repository.LocationTrackRepository;

@RestController
@RequestMapping("/api")
public class LocationTrackController {

    @Autowired
    private LocationTrackService locationTrackService;

    private final SimpMessagingTemplate messagingTemplate;
    private final LocationTrackRepository repository;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LocationTrackController(SimpMessagingTemplate messagingTemplate, LocationTrackRepository repository) {
        this.messagingTemplate = messagingTemplate;
        this.repository = repository;
    }

    // 1. Filter surveyors
    @GetMapping("/surveyors/filter")
    public List<Surveyor> filterSurveyors(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String status
    ) {
        System.out.println("Filtering surveyors with city: " + city + ", project: " + project + ", status: " + status);
        return locationTrackService.filterSurveyors(city, project, status);
    }

    // 2. Get latest location of a surveyor
    @GetMapping("/location/{surveyorId}/latest")
    public LocationTrack getLatestLocation(@PathVariable String surveyorId) {
        return locationTrackService.getLatestLocation(surveyorId);
    }

    // 3. Get track history for a surveyor
    @GetMapping("/location/{surveyorId}/track")
    public List<LocationTrack> getTrackHistory(
            @PathVariable String surveyorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<LocationTrack> locationTracklist = locationTrackService.getTrackHistory(surveyorId, start, end);
        System.out.println("Track history for surveyor " + surveyorId + ": " + locationTracklist);
        return locationTracklist;
    }

    // 4. Get online/offline status for all surveyors
    @GetMapping("/surveyors/status")
    public java.util.Map<String, String> getSurveyorStatus() {
        // Example logic: mark all surveyors as 'Online' (replace with real logic as needed)
        List<Surveyor> surveyors = locationTrackService.filterSurveyors(null, null, null);
        java.util.Map<String, String> statusMap = new java.util.HashMap<>();
        for (Surveyor s : surveyors) {
            statusMap.put(s.getId(), "Online"); // Replace with real status check
        }
        return statusMap;
    }

    @PostMapping("live/location")
    public void publishLiveLocation(@RequestBody LiveLocationMessage message) {
        // 1. Broadcast via WebSocket as JSON string
        try {
            String json = objectMapper.writeValueAsString(message);
            System.out.println("Broadcasting live location: " + json);
            messagingTemplate.convertAndSend("/topic/location/" + message.surveyorId, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 2. Save to DB (geom set to null to avoid PostGIS error)
        LocationTrack entity = new LocationTrack(message.surveyorId, message.latitude, message.longitude, message.timestamp, null);
        repository.save(entity);
    }
    
}






