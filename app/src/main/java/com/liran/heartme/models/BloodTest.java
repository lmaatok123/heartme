package com.liran.heartme.models;

/**
 * Created by liran on 6/11/18.
 */

public class BloodTest {
    private String name;
    private int threshold;

    public BloodTest(String name,int threshold){
        this.name= name;
        this.threshold=threshold;
    }

    public String getName() {
        return name;
    }

    public int getThreshold() {
        return threshold;
    }

}
