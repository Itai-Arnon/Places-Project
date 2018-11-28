package com.project.itai.FindAPlace.beans;

import java.io.Serializable;
import java.util.Objects;



public class Place implements Serializable {
    private Integer id;
    private String name;
    private double lat;
    private double lng;
    private String address;
    private String city;
    private String urlImage;

    public Place(Integer id, String name, double lat, double lng, String address, String city, String urlImage) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.city = city;
        this.urlImage = urlImage;
    }


    public Place() {  }

    public Place(String name, double lat, double lng, String address, String city, String urlImage) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.city = city;
        this.urlImage = urlImage;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Integer getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getUrlImage() {
        return urlImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Place)) return false;
        Place place = (Place) o;
        return Double.compare(place.lat, lat) == 0 &&
                Double.compare(place.lng, lng) == 0 &&
                Objects.equals(id, place.id) &&
                Objects.equals(name, place.name) &&
                Objects.equals(address, place.address) &&
                Objects.equals(city, place.city) &&
                Objects.equals(urlImage, place.urlImage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, lat, lng, address, city, urlImage);
    }

    //default

        @Override
        public String toString() {
            return name ;
        }


        public String toString2() {
            return "Place{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }
        public String toString3() {
            return "Place{" +
                    " name='" + name + '\'' +
                    ", lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }
    }





