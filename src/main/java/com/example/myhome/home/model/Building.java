package com.example.myhome.home.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// --- ДОМА ---

@Data
@Entity
@Table(name = "buildings")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    private List<BuildingSection> sections  = new ArrayList<>();

    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    private List<BuildingFloor> floors = new ArrayList<>();

    @OneToMany(mappedBy = "building")
    List<Apartment> apartments;

    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String img5;

    @ManyToMany
    @JoinTable(name="building_admins",
            joinColumns = @JoinColumn(name="building_id"),
            inverseJoinColumns = @JoinColumn(name="admin_id"))
    List<Admin> admins;

    public Building() {
    }

    public Building(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

