package com.mdstech.sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class RxJavaDemoTest {

    @Test
    public void testProcess() throws ExecutionException, InterruptedException {

        RxJavaDemo rxJavaDemo = new RxJavaDemo();
        CompletableFuture<Long> timeCF = rxJavaDemo.process();

        assertThat("Expected time should be less than 90s", 90000000l, greaterThan(timeCF.get()));

    }

}
