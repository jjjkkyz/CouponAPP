package com.edu.pu.cs.couponapp.Bean;

public class Bound {
    private Double latitude;
    private Double longitude;
    private Double bound;
    private Double longitude_per_m;
    private Double latitude_per_m;


    public Bound(Double lat, Double lon, Double b){
        this.latitude=lat;
        this.longitude=lon;
        this.bound=b;

        //地球周长
        Double perimeter =  2*Math.PI*6371000;
        //纬度latitude的地球周长：latitude
        Double perimeter_latitude =   perimeter*Math.cos(Math.PI * latitude / 180);
        this.longitude_per_m = 360 / perimeter_latitude;
        this.latitude_per_m = 360 /perimeter;
    }

    public Double getLeftLo(){
        return longitude-(bound*longitude_per_m);
    }

    public Double getRightLo(){
        return longitude+(bound*longitude_per_m);
    }

    public Double getTopLa(){
        return latitude+(bound*latitude_per_m);
    }

    public Double getBottomLa(){
        return latitude-(bound*latitude_per_m);
    }
}
