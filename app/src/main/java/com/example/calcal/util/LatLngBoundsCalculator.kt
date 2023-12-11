package com.example.calcal.util

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds

class LatLngBoundsCalculator {

    companion object {

        fun calculateBounds(coords: List<LatLng>): LatLngBounds {
            var minLat = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var minLng = Double.MAX_VALUE
            var maxLng = Double.MIN_VALUE

            for (coord in coords) {
                minLat = minOf(minLat, coord.latitude)
                maxLat = maxOf(maxLat, coord.latitude)
                minLng = minOf(minLng, coord.longitude)
                maxLng = maxOf(maxLng, coord.longitude)
            }

            return LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng))
        }
    }
}