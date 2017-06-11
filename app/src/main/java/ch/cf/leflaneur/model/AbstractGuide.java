package ch.cf.leflaneur.model;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import ch.cf.leflaneur.MapsActivity;
import ch.cf.leflaneur.R;
import ch.cf.leflaneur.enums.Direction;
import ch.cf.leflaneur.enums.Direction2Image;
import ch.cf.leflaneur.output.Audio;

public abstract class AbstractGuide {
    protected List<WayPoint> wayPoints;
    protected Location currentLocation;
    protected PolylineOptions polylineOptions;
    private String text1;
    private String text2;
    private int imageNumber;

    protected Activity activity;
    private Audio audio;
    private boolean isOutputAudio = false;

    public AbstractGuide(Activity activity, TextToSpeech tts) {
        wayPoints = new ArrayList<WayPoint>();
        this.activity = activity;
        this.audio = new Audio(tts);
    }

    public void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }

    public boolean isOutputAudio() {
        return isOutputAudio;
    }

    public void setOutputAudio(boolean outputAudio) {
        isOutputAudio = outputAudio;
    }

    public int getImageNumber() {
        return imageNumber;
    }

    public void checkLocation(Location currentLocation){
        this.currentLocation = currentLocation;

        if (this.wayPoints.equals(null)){
            return;
        }

        List<WayPoint> tmpWayPoints = new ArrayList<WayPoint>();

        for (WayPoint waypoint : this.wayPoints){
            double distanceBetween = waypoint.getLocation().distanceTo(this.currentLocation);


            if (distanceBetween < this.activity.getResources().getInteger(R.integer.distance_between_points) && distanceBetween < waypoint.getDistanceToNeighbor()){
                this.ouput(waypoint.getDirection());
                this.outputNotification(waypoint.getManeuver(), waypoint.getInstruction(), R.drawable.leflaneursoundon); //TODO set image
                if (this.isOutputAudio()){
                    this.outputAudio(waypoint.getInstruction());
                }
                this.text1 = waypoint.getManeuver();
                this.text2 = waypoint.getInstruction();
                this.imageNumber = Direction2Image.getImageName(waypoint.getDirection());
            }else{
                tmpWayPoints.add(waypoint);
            }
        }
        this.setWayPoints(tmpWayPoints);
    }

    public boolean isOnPath(Location currentLocation){
        double smallestDistance = this.getSmallestDistanceToPolyline(currentLocation);
        boolean bReturn = true;
        if (smallestDistance > this.activity.getResources().getInteger(R.integer.distance_to_be_still_on_path)){
            bReturn = false;
        }
        return bReturn;
    }

    /**
     *  Function:   Get smallest distanc of LocationC to two following Points of the PolyLine
     */
    private double getSmallestDistanceToPolyline(Location locationC){
        double smallestDistance = -1;
        List<LatLng> points = this.polylineOptions.getPoints();

        for (int i = 1; i < points.size(); i++){

            LatLng pointA = points.get(i-1);
            Location locationA = new Location("a");
            locationA.setLatitude(pointA.latitude);
            locationA.setLongitude(pointA.longitude);

            LatLng pointB = points.get(i);
            Location locationB = new Location("b");
            locationB.setLatitude(pointB.latitude);
            locationB.setLongitude(pointB.longitude);

            double a = locationB.distanceTo(locationC);
            double b = locationC.distanceTo(locationA);
            double c = locationA.distanceTo(locationB);

            double alpha = Math.acos( (b*b + c*c - a*a) / (2*b*c) );
            double beta = Math.acos( (a*a + c*c - b*b) / (2*a*c) );
            double distance = b * Math.sin(alpha);

            // Wenn der Winkel alpha oder beta grösser als 90° ist,
            // dann ist die Höhe C nicht mehr repräsentativ für die Distanz zur Seite C.
            if (Math.toDegrees(alpha) > 90 || Math.toDegrees(beta) > 90){
                if (a > b){
                    distance = b;
                }else{
                    distance = a;
                }
            }

            if (smallestDistance == -1 || smallestDistance > distance){
                smallestDistance = distance;
            }
        }

        return smallestDistance;
    }

    public void setFirstWayPoint(){
        if (this.wayPoints.size() > 0){
            WayPoint waypoint = this.wayPoints.get(0);
            this.text1 = waypoint.getManeuver();
            this.text2 = waypoint.getInstruction();
            this.imageNumber = Direction2Image.getImageName(waypoint.getDirection());
            this.wayPoints.remove(waypoint);
        }
    }

    abstract protected void ouput(Direction direction);

    private void outputAudio(String text){
        this.audio.speakOut(text);
    }

    private void outputNotification(String mainText, String subText, int drawable){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.activity)
                        .setSmallIcon(R.drawable.marker_green)
                        .setLargeIcon(BitmapFactory.decodeResource(this.activity.getResources(),
                                drawable))
                        .setContentTitle(mainText)
                        .setContentText(subText);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this.activity, MapsActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.activity);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MapsActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) this.activity.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }

    public void clearAllNotifications(){
        NotificationManager nMgr = (NotificationManager) this.activity.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
