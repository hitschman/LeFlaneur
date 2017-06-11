//TODO 0: Template for Source Documentation
//TODO 0: Write SourceDocumentation
//DONE 1: Integrate LongClick and FindAdresse


//DONE 4: Start to build LogicalUnit and Classes (check Navigation Class: all information available?)
//DONE 5: Parse DirectionJSON and fill LogicalUnit (already done with Long Click and find address)
//DONE 6: Draw Route to Map
//DONE 7: Integrate Guide Mode
//DONE 10: Redraw UI Elements

//TODO 8: Integrate ShortClick Show search Field
//TODO 9: Integrate SearchTable and ClickEvent




package ch.cf.leflaneur;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.cf.leflaneur.backgroundTasks.DirectionDownloadTask;
import ch.cf.leflaneur.enums.Direction;
import ch.cf.leflaneur.enums.LocationUpdateInterval;
import ch.cf.leflaneur.model.AppState;
import ch.cf.leflaneur.model.Navigation;
import ch.cf.leflaneur.model.WayPoint;


public class MapsActivity extends AbstractMapsActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private AppState appState = new AppState();

    private LinearLayout debugLayout;

    private RelativeLayout summaryLayout;
    private TextView summaryTimeDistanceView;
    private TextView summaryDirectionView;

    private RelativeLayout guideBottomLayout;
    private FrameLayout guideTopLayout;

    private LinearLayout topLayout;

    public void onConnected(Bundle connectionHint){
        super.onConnected(connectionHint);

        this.debugLayout = (LinearLayout) findViewById(R.id.debugLayout);

        this.guideBottomLayout = (RelativeLayout) findViewById(R.id.guideBottomLayout);
        this.guideTopLayout = (FrameLayout) findViewById(R.id.guideTopLayout);

        this.summaryLayout = (RelativeLayout) findViewById(R.id.summaryLayout);
        this.summaryDirectionView  = (TextView) findViewById(R.id.summaryDirectionView);
        this.summaryTimeDistanceView = (TextView) findViewById(R.id.summaryTimeDistanceView);

        this.topLayout = (LinearLayout) findViewById(R.id.topLayout);

        this.updateUI();
    }

    public void onMapClick(LatLng latLng) {
        super.onMapClick(latLng);
        Log.d(TAG, "onMapClick: "+latLng.toString());
        //TODO search map

        /*Location l = new Location("ASDF");
        l.setLatitude(latLng.latitude);
        l.setLongitude(latLng.longitude);
        onLocationChanged(l);/**/
    }


    public void onMapLongClick(LatLng latLng) {
        if (!this.appState.isGuideMode()) {
            super.onMapLongClick(latLng);
            Log.d(TAG, "onMapLongClick: " + latLng.toString());

            this.calculateRoute(latLng);
        }
    }

    public void calculateRoute(LatLng latLng){
        Log.d(TAG, "recalculateRoute: " + latLng.toString());
        new DirectionDownloadTask(this, this.currentLatLng, latLng).execute();
    }

    public void updateUI(){

        if (this.appState.isDebugMode()){
            this.debugLayout.setVisibility(LinearLayout.INVISIBLE);
        }else{
            this.debugLayout.setVisibility(LinearLayout.INVISIBLE);
        }

        if (this.appState.isSummaryMode()){
            this.summaryLayout.setVisibility(RelativeLayout.VISIBLE);
        }else{
            this.summaryLayout.setVisibility(RelativeLayout.INVISIBLE);
        }

        if (this.appState.isSearchMode()){
            this.topLayout.setVisibility(LinearLayout.VISIBLE);
        }else{
            this.topLayout.setVisibility(LinearLayout.INVISIBLE);
        }

        if (this.appState.isGuideMode()){
            this.guideBottomLayout.setVisibility(RelativeLayout.VISIBLE);
            this.guideTopLayout.setVisibility(FrameLayout.VISIBLE);

            TextView field1 = (TextView) this.guideTopLayout.findViewById(R.id.field1TextView);
            TextView field2 = (TextView) this.guideTopLayout.findViewById(R.id.field2TextView);
            ImageView image = (ImageView) this.guideTopLayout.findViewById(R.id.commandImage);

            field1.setText(this.guide.getText1());
            field2.setText(this.guide.getText2());
            image.setImageResource(this.guide.getImageNumber());
        }else{
            this.guideBottomLayout.setVisibility(RelativeLayout.INVISIBLE);
            this.guideTopLayout.setVisibility(FrameLayout.INVISIBLE);
        }
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
        this.appState.setSummaryMode(this.navigation);

        if (this.appState.isSummaryMode()) {
            this.summaryDirectionView.setText("via " + this.navigation.getStartAddressStreet());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                this.summaryTimeDistanceView.setText(Html.fromHtml("<b>" + navigation.getTime() + " min </b> (" + navigation.getDistance() + "m)", Html.FROM_HTML_MODE_COMPACT));
            } else {
                this.summaryTimeDistanceView.setText(Html.fromHtml("<b>" + navigation.getTime() + " min </b> (" + navigation.getDistance() + "m)"));
            }
        }

        if (this.appState.isGuideMode()){
            this.setGuideModeReady();
        }
    }

    public void drawMap(){
        super.drawMap();

        Bitmap icon_green = BitmapFactory.decodeResource(getResources(), R.drawable.marker_green);
        Bitmap icon_black = BitmapFactory.decodeResource(getResources(), R.drawable.marker_black);
        Bitmap newIconGreen = Bitmap.createScaledBitmap(icon_green, icon_green.getWidth() / (icon_green.getHeight()/160), 160, false);
        Bitmap newIconBlack = Bitmap.createScaledBitmap(icon_black, icon_black.getWidth() / (icon_black.getHeight()/80), 80, false);

        this.mMap.addPolyline(this.navigation.getPolylineOptions());



        for (WayPoint wp : this.navigation.getWayPoints()) {
            if (!wp.getDirection().equals(Direction.START) && !wp.getDirection().equals(Direction.STOP)) {

                if (getResources().getBoolean(R.bool.draw_marker_to_waypoint)) {
                    this.mMap.addMarker(new MarkerOptions().position(wp.getLatLng()).title(wp.getManeuver()).snippet(wp.getInstruction()).icon(BitmapDescriptorFactory.fromBitmap(newIconBlack)));
                }

                if (getResources().getBoolean(R.bool.draw_circle_around_turn)) {
                    this.mMap.addCircle(new CircleOptions()
                            .center(wp.getLatLng())
                            .radius(wp.getDistanceToNeighbor() < getResources().getInteger(R.integer.distance_between_points) ?
                                    wp.getDistanceToNeighbor() :
                                    getResources().getInteger(R.integer.distance_between_points))
                            .strokeColor(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                    getResources().getColor(R.color.colorFlaneurGreen, getTheme()) :
                                    getResources().getColor(R.color.colorFlaneurGreen))
                            .strokeWidth(4)
                            .fillColor(Color.TRANSPARENT));
                }

            }else if (wp.getDirection().equals(Direction.STOP)) {

                this.mMap.addMarker(new MarkerOptions().position(wp.getLatLng()).title(wp.getManeuver()).snippet(wp.getInstruction()).icon(BitmapDescriptorFactory.fromBitmap(newIconGreen)));

                if (getResources().getBoolean(R.bool.draw_circle_around_end)){
                    this.mMap.addCircle(new CircleOptions()
                            .center(wp.getLatLng())
                            .radius(wp.getDistanceToNeighbor() < getResources().getInteger(R.integer.distance_between_points) ?
                                    wp.getDistanceToNeighbor() :
                                    getResources().getInteger(R.integer.distance_between_points))
                            .strokeColor(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                    getResources().getColor(R.color.colorFlaneurGreen, getTheme()) :
                                    getResources().getColor(R.color.colorFlaneurGreen))
                            .strokeWidth(4)
                            .fillColor(Color.TRANSPARENT));
                }
            }
        }

        if(this.appState.isDebugMode()) {
            this.mMap.addCircle(new CircleOptions()
                    .center(this.currentLatLng)
                    .radius(10)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.BLUE));
        }

    }



    public void changeGuideStateButton(View view){
        this.appState.setGuideMode(!this.appState.isGuideMode());
        this.guide.clearAllNotifications();
        this.setGuideModeReady();
    }

    public void setGuideModeReady(){
        if (this.appState.isGuideMode()){
            this.guide.setWayPoints(this.navigation.getWayPoints());
            this.guide.setPolylineOptions(this.navigation.getPolylineOptions());
            this.guide.setFirstWayPoint();
            this.startBackgroundLocationUpdate();
            this.updateLocationUpdateInterval(LocationUpdateInterval.FAST);
        }else{
            this.appState.setSummaryMode(this.navigation);
            this.stopBackgroundLocationUpdate();
            this.updateLocationUpdateInterval(LocationUpdateInterval.SLOW);
            this.guide.setWayPoints(null);
            this.guide.setPolylineOptions(null);
        }
        this.updateUI();
    }

    public void onLocationChanged(Location location){
        this.addTextDebug(location.toString());

        if (this.appState.isSummaryMode() && this.currentLocation.distanceTo(location) == getResources().getInteger(R.integer.distance_for_recalculation)){
            super.onLocationChanged(location);
            this.calculateRoute(this.navigation.getLocationTo());
        }else{
            super.onLocationChanged(location);
        }

        if (this.appState.isGuideMode()){
            if (this.guide.isOnPath(this.currentLocation)){
                this.guide.checkLocation(this.currentLocation);
            } else {
                this.calculateRoute(this.navigation.getLocationTo());
            }
            this.updateUI();
        }
    }

    public void addTextDebug(String text){
        TextView field = (TextView) this.debugLayout.findViewById(R.id.debugText);
        field.setText(text + "\n" + field.getText());
    }

    public void changeAudioState(View view){

        ImageView iv = (ImageView) view;

        this.appState.setAudioMode(!this.appState.isAudioMode());

        this.guide.setOutputAudio(this.appState.isAudioMode());

        if (this.appState.isAudioMode()){
            iv.setImageResource(R.drawable.leflaneursoundon);
        }else{
            iv.setImageResource(R.drawable.leflaneursoundoff);
        }
    }

    public void changeSearchState(View view){
        this.appState.setSearchMode(!this.appState.isSearchMode());
        this.updateUI();
    }
}
