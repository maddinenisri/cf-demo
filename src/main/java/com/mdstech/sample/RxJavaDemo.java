package com.mdstech.sample;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class RxJavaDemo {

    public CompletableFuture<Long> process() {
        long start = System.currentTimeMillis();
        CompletableFuture<Long> completableFuture = new CompletableFuture<>();
        Observable<Car> carObservable = cars();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        Flowable<Car> carFlowable = carObservable.toFlowable(BackpressureStrategy.BUFFER);
        final Map<String, Long> timeMap = new ConcurrentHashMap<>();
        final int totalCount[] = new int[1];
        carFlowable.buffer(5000)
                .parallel(8)
                .runOn(Schedulers.io())
                .map(c -> RateCalculator.getRate(c, timeMap))
                .sequential()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(Schedulers.computation())
                .doOnComplete(() -> {long end = System.currentTimeMillis(); System.out.println("Actual time Took " + (end - start) + "ms.");})
                .subscribe(cars -> {
                    totalCount[0] = totalCount[0]+cars.size();
                    System.out.println(String.format("[%s] size: %d", Thread.currentThread().getName(), totalCount[0]));
//                    System.out.println(timeMap);
                },this::onError, () -> {long end = System.currentTimeMillis(); System.out.println("Actual time Took ... " + (end - start) + "ms."); completableFuture.complete((end - start));});

        completableFuture.join();
        return completableFuture;

    }

    private void onError(Throwable e)
    {
        long start = System.currentTimeMillis();
        done(start);
    }

    private void done(long start) {
        long end = System.currentTimeMillis();
        System.out.println("Actual time Took " + (end - start) + "ms.");

    }

    private Observable<Car> cars() {
        return Observable.fromIterable(LongStream.range(1, 1000000).mapToObj(this::getCar).collect(Collectors.toList()));
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
