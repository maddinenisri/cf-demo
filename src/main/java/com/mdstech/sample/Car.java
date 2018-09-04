package com.mdstech.sample;

import lombok.*;

@NoArgsConstructor
@ToString
public class Car {

    @Builder
    public Car(long id, ManufacturerType manufacturer, String model, Integer year) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
    }

    @Getter
    private long id;
    @Getter
    private ManufacturerType manufacturer;
    @Getter
    private String model;
    @Getter
    private Integer year;
    @Setter
    @Getter
    private Double rating;
}
