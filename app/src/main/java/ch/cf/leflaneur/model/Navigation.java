package ch.cf.leflaneur.model;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by christianfallegger on 07.04.17.
 */

public class Navigation {
    private LatLng locationFrom, locationTo, currentLocation;
    private List<WayPoint> wayPoints;
    private int distance;
    private int time;
    private String startAddress;
    private String endAddress;
    private PolylineOptions polylineOptions;

    public Navigation() {
        this.wayPoints = new ArrayList<WayPoint>();
        this.locationFrom = new LatLng(0,0);
        this.locationTo = new LatLng(0,0);
        this.distance = 0;
        this.time = 0;
        this.startAddress = "";
        this.endAddress = "";
        this.polylineOptions = new PolylineOptions();
    }


    public LatLng getLocationFrom()
    {
        return locationFrom;
    }

    public LatLng getLocationTo()
    {
        return locationTo;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setLocationFrom(LatLng locationFrom)
    {
        this.locationFrom = locationFrom;
    }

    public void setLocationTo(LatLng locationTo)
    {
        this.locationTo = locationTo;
    }

    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
        this.checkWayPoints();
    }

    public void setWayPoints(WayPoint wayPoint) {
        this.wayPoints.add(wayPoint);
        this.checkWayPoints();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getStartAddressStreet() {
        return ((String []) ((String []) startAddress.split(","))[0].split(" "))[0];
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    private void checkWayPoints(){

        List<WayPoint> wayPoints = new ArrayList<WayPoint>();


        for (int i = 1; i <this.wayPoints.size(); i++){
            WayPoint wp1 = this.wayPoints.get(i-1);
            WayPoint wp2 = this.wayPoints.get(i);

            if (i == 1){
                wayPoints.add(wp1);
            }
            Location from = new Location("from service");
            from.setLatitude(wp1.getLatLng().latitude);
            from.setLongitude(wp1.getLatLng().longitude);

            Location to = new Location("to service");
            to.setLatitude(wp2.getLatLng().latitude);
            to.setLongitude(wp2.getLatLng().longitude);

            wp2.setDistanceToNeighbor(from.distanceTo(to));
            wayPoints.add(wp2);
        }
        this.wayPoints = wayPoints;
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setPolylineOptions(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
            this.polylineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            this.polylineOptions.addAll(points);
            this.polylineOptions.width(6);
            this.polylineOptions.color(Color.parseColor("#009297"));
        }
    }



    public boolean isRouteSet(){
        if(this.distance != 0 && this.time != 0){
            return true;
        }
        return false;
    }
}
