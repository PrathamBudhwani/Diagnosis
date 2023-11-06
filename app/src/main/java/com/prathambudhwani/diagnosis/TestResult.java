package com.prathambudhwani.diagnosis;

public class TestResult {
    private String testName;
    private String result;
    private String deviceName;
    private long timestamp;

    // Constructors, getters, setters

    public TestResult() {

    }

    public TestResult(String testName, String result,String deviceName, long timestamp) {
        this.testName = testName;
        this.result = result;
        this.timestamp = timestamp;
        this.deviceName = deviceName;
    }


    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
