package com.mdstech.sample;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
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


    public static List<Car> getRateUsingEndPoint(List<Car> cars, Map<String, Long> timeMap) {
        long start = System.currentTimeMillis();
        final List<Car> updatedCars = new ArrayList<>();
        Observable<Car> carObservable = Observable.fromIterable(cars);
        CompletableFuture<List<Car>> completableFuture = new CompletableFuture<>();
        carObservable.buffer(500).map(carList -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<List<Car>> requestBody = new HttpEntity<>(carList, headers);
            ResponseEntity<Car[]> responseEntity = restTemplate.exchange("http://localhost:8080/api/rate", HttpMethod.POST, requestBody, Car[].class);
            return Arrays.asList(responseEntity.getBody());
        }).subscribeOn(Schedulers.newThread()).subscribe(uCars -> {updatedCars.addAll(uCars);}, throwable -> {throw new RuntimeException(throwable);}, ()-> {completableFuture.complete(updatedCars);});
        completableFuture.join();
        try {
            long end = System.currentTimeMillis();
            if(!timeMap.containsKey(Thread.currentThread().getName())) {
                timeMap.put(Thread.currentThread().getName(), end - start);
            }
            else {
                timeMap.put(Thread.currentThread().getName(), timeMap.get(Thread.currentThread().getName()) + (end - start));
            }
            return completableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
