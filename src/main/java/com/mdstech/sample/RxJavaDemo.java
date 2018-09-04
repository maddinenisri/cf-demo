package com.mdstech.sample;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class RxJavaDemo {

    public long process() {
        long start = System.currentTimeMillis();
        Observable<Car> carObservable = cars();
        Flowable<Car> carFlowable = carObservable.toFlowable(BackpressureStrategy.BUFFER);
//        carFlowable.parallel().runOn(Schedulers.computation())
        carObservable.buffer(5000)
                .map(c -> RateCalculator.getRate(c))
                .subscribe(cars -> { cars.stream().forEach(car -> System.out.println(car.getId()+":"+car.getRating()));});
        long end = System.currentTimeMillis();
        System.out.println("Took " + (end - start) + "ms.");
        return (end - start);

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
