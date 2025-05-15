package com.neogeo.tracking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "surveyor")
public class Surveyor {

    @Id
    @Column(name = "id")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String city;

    @Column(name = "project_name")
    private String projectName;

    // Constructors
    public Surveyor() {}

    public Surveyor(String id, String name, String city, String projectName) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.projectName = projectName;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}

