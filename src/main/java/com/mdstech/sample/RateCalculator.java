package com.mdstech.sample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RateCalculator {
    private static Map<String, Double> rateSheet = new HashMap<>();

    static  {
        rateSheet.put("Honda_Accord", 3.05d);
        rateSheet.put("Honda_Civic", 2.55d);
        rateSheet.put("Honda_Odyssey", 4.95d);
        rateSheet.put("Honda_Pilot", 5.15d);

        rateSheet.put("GM_Bolt", 3.05d);
        rateSheet.put("GM_Impala", 5.05d);
        rateSheet.put("GM_Cadilac", 6.25d);
        rateSheet.put("GM_Buick", 4.35d);

        rateSheet.put("Toyota_Camry", 3.65d);
        rateSheet.put("Toyota_Carola", 2.55d);
        rateSheet.put("Toyota_Highlander", 5.15d);
        rateSheet.put("Toyota_Sienna", 4.55d);
    }

    public static CompletionStage<Double> getRate(long id, String manufacturer, String model) {
        //ExecutorService executorService = Executors.newFixedThreadPool(100);
        return CompletableFuture.supplyAsync(() -> {
            if(id > 0l && id%500 == 0) {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                }
            }
            return rateSheet.get(String.format("%s_%s", manufacturer, model));
        }).exceptionally(th -> -1d);
    }

    public static double getRate(String manufacturer, String model) {
        return rateSheet.get(String.format("%s_%s", manufacturer, model));
    }

    public static List<Car> getRate(List<Car> cars, Map<String, Long> timeMap) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(6000);
        } catch (Exception ex) {
        }
//        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE) + ": ["+Thread.currentThread().getName()+"] size:"+cars.size());
        List<Car> updatedCars =  cars.stream().map(c -> {
            c.setRating(rateSheet.get(String.format("%s_%s", c.getManufacturer(), c.getModel())));
            return c;
        }
        ).collect(Collectors.toList());
        long end = System.currentTimeMillis();
        if(!timeMap.containsKey(Thread.currentThread().getName())) {
            timeMap.put(Thread.currentThread().getName(), end - start);
        }
        else {
            timeMap.put(Thread.currentThread().getName(), timeMap.get(Thread.currentThread().getName()) + (end - start));
        }
        return updatedCars;
    }

}
