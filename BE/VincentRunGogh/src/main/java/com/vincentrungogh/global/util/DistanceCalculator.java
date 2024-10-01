package com.vincentrungogh.global.util;

public class DistanceCalculator {

    private static final int EARTH_RADIUS = 6371000;

    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        // harversine 공식
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        // 미터를 km로 변환하고 소수점 둘째 자리까지 반올림
        return Math.round((distance / 1000) * 100.0) / 100.0;
    }
}
