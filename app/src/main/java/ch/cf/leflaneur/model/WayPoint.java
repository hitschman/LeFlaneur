package ch.cf.leflaneur.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ch.cf.leflaneur.enums.Direction;
import ch.cf.leflaneur.enums.Maneuver2Direction;

public class WayPoint
{
    private LatLng latLng;
    private Location location;
    private int distance = 0;
    private int duration = 0;
    private String instruction = "";
    private String instructionHtml = "";
    private String maneuver = "";
    private Direction direction = Direction.FAULT;
    private String travelMode = "";
    private String polyline = "";
    private List<LatLng> polyLinePoints = new ArrayList<LatLng>();
    private double distanceToNeighbor = Integer.MAX_VALUE;



    public WayPoint(WayPoint wp) {
        this.location = wp.location;
        this.distance = wp.distance;
        this.duration = wp.duration;
        this.instruction = wp.instruction;
        this.instructionHtml = wp.instructionHtml;
        this.maneuver = wp.maneuver;
        this.direction = wp.direction;
        this.travelMode = wp.travelMode;
        this.polyline = wp.polyline;
        this.polyLinePoints = wp.polyLinePoints;
        this.distanceToNeighbor = wp.distanceToNeighbor;
    }

    public WayPoint() {
    }

    public List<LatLng> getPolyLinePoints() {
        return polyLinePoints;
    }

    public void setPolyLinePoints(List<LatLng> polyLinePoints) {
        this.polyLinePoints = polyLinePoints;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation(){
        return this.location;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getInstructionHtml() {
        return instructionHtml;
    }

    public void setInstructionHtml(String instructionHtml) {
        this.instructionHtml = instructionHtml;
        try {
            this.instruction = instructionHtml.replaceAll("\\<\\/?div[^>]*\\>", ".");
            this.instruction = this.instruction.replaceAll("\\<[^>]*>", " ");
            this.instruction = this.instruction.replace(" .", ". ");
            this.instruction = this.instruction.replaceAll("\\/.*", "");
        }catch(Exception e){
            e.printStackTrace();
            this.instruction = this.instructionHtml;
        }
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.direction = Maneuver2Direction.getDirection(maneuver);
        this.maneuver = maneuver.toUpperCase();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public double getDistanceToNeighbor() {
        return distanceToNeighbor;
    }

    public void setDistanceToNeighbor(double distanceToNeighbor) {
        this.distanceToNeighbor = distanceToNeighbor;
    }
}
