package com.mdstech.sample;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CfDemo {


    public long process() {
        long start = System.currentTimeMillis();

        CompletableFuture.supplyAsync(() -> cars())
                .thenCompose(cars -> {
                    List<CompletionStage<Car>> updatedCars =
                            cars.stream()
                                    .map(c -> RateCalculator.getRate(c.getId(), c.getManufacturer().name(), c.getModel()).thenApply(r -> {
                                        c.setRating(r);
                                        return c;
                                    })).collect(Collectors.toList());

                    CompletableFuture<Void> done =
                            CompletableFuture.allOf(updatedCars.toArray(new CompletableFuture[updatedCars.size()]));

                    return done.thenApply(v -> updatedCars.stream().map(CompletionStage::toCompletableFuture).map(CompletableFuture::join).collect(Collectors.toList()));
                }).whenComplete((cars, th) -> {
                    if(th == null) {
                        cars.forEach(System.out::println);
                    }
                    else {
                        throw new RuntimeException(th);
                    }

        }).toCompletableFuture().join();

        long end = System.currentTimeMillis();
        System.out.println("Took " + (end - start) + "ms.");
        return (end - start);
    }

    private List<Car> cars() {
        return LongStream.range(1, 5000000).mapToObj(this::getCar).collect(Collectors.toList());
    }

    private Car getCar(long index) {
        ManufacturerType manufacturerType = getManufacturerType();
        return Car.builder().id(index).year(getRandomYear()).model(getModel(manufacturerType)).manufacturer(manufacturerType).build();
    }

    private ManufacturerType getManufacturerType() {
        return ManufacturerType.values()[new Random().nextInt(ManufacturerType.values().length-1-0) + 0];
    }

    private String getModel(ManufacturerType manufacturerType) {
        List<String> models = ManufacturerType.models(manufacturerType);
        return models.get(new Random().nextInt(models.size()-1-0) + 0);
    }

    private Integer getRandomYear() {
        return new Random().nextInt(2018-1990) + 1990;
    }
}
