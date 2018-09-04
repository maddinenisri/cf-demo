package com.mdstech.sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
@RunWith(JUnit4.class)
public class CfDemoTest {

    @Test
    public void testProcess() {
        CfDemo cfDemo = new CfDemo();
        long time = cfDemo.process();
        try {
            Thread.sleep(50000);
        } catch(Exception ex) {}
        assertThat("Expected time should be less than 90s", 90000000l, greaterThan(time));
    }


}
