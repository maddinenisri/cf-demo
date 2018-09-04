package com.mdstech.sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class RxJavaDemoTest {

    @Test
    public void testProcess() {
        try {
            Thread.sleep(50000);
        } catch(Exception ex) {}

        RxJavaDemo rxJavaDemo = new RxJavaDemo();
        long time = rxJavaDemo.process();
        try {
            Thread.sleep(50000);
        } catch(Exception ex) {}
        assertThat("Expected time should be less than 90s", 90000000l, greaterThan(time));
    }

}
