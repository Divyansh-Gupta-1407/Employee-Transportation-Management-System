package com.ets.entity;

import com.ets.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String model;

    private int capacity;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Builder.Default
    @Column(name = "is_active")
    private boolean active = true;
}
