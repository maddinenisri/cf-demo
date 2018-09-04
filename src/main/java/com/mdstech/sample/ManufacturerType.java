package com.mdstech.sample;

import java.util.List;

import static java.util.Arrays.asList;

public enum ManufacturerType {
    Honda, GM, Toyota;

    public static List<String> models(ManufacturerType manufacturerType) {
        switch (manufacturerType) {
            case Honda:
                return asList("Accord", "Civic", "Odyssey", "Pilot" );
            case GM:
                return asList("Bolt", "Impala", "Cadilac", "Buick" );
            case Toyota:
                return asList("Camry", "Carola", "Highlander", "Sienna" );
            default:
                return asList();
        }
    }
}
