package org.woheller69.lavatories.database;

/**
 * This class is the database model for the stations.
 */
public class Station {

    private int id;
    private int city_id;
    private long timestamp;
    private boolean wheelchair;
    private boolean babyChanging;
    private boolean paid;
    private String operator;
    private String openingHours;
    private String address1;
    private String address2;
    private double distance;
    private double latitude;
    private double longitude;
    private String uuid;


    public Station() {
    }

    public Station(int id, int city_id, long timestamp, boolean wheelchair, boolean babyChanging, boolean paid, String operator, String openingHours, String address1, String address2, double distance, double latitude, double longitude, String uuid) {
        this.id = id;
        this.city_id = city_id;
        this.timestamp = timestamp;
        this.wheelchair = wheelchair;
        this.babyChanging = babyChanging;
        this.paid = paid;
        this.operator = operator;
        this.openingHours = openingHours;
        this.address1 = address1;
        this.address2 = address2;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(boolean wheelchair) {
        this.wheelchair = wheelchair;
    }

    public boolean isBabyChanging() {
        return babyChanging;
    }

    public void setBabyChanging(boolean babyChanging) {
        this.babyChanging = babyChanging;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean open) {
        paid = open;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String brand) {
        this.operator = brand;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String name) {
        this.openingHours = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
