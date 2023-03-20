package com.example.myhome.home.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

// --- ЗАЯВКИ ВЫЗОВА МАСТЕРА ---

@Data
@Entity
@Table(name = "repair_requests")
public class RepairRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime best_time;

    @Enumerated(EnumType.STRING)
    private RepairMasterType master_type;

    private String description, comment;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    private String phone_number;

    @ManyToOne
    @JoinColumn(name = "master_id")
    private User master;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime request_date;

    @Enumerated(EnumType.STRING)
    private RepairStatus repairStatus;


}
