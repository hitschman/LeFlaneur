package ch.cf.leflaneur.backgroundTasks;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import ch.cf.leflaneur.MapsActivity;
import ch.cf.leflaneur.R;
import ch.cf.leflaneur.model.Navigation;
import ch.cf.leflaneur.parser.DirectionsJSONParser;

public class DirectionDownloadTask extends AbstractDownloadTask {

    private static final String TAG = DirectionDownloadTask.class.getSimpleName();
    private String url = "";
    private LatLng startLatLng = new LatLng(0, 0);
    private LatLng destLatLng = new LatLng(0, 0);


    public DirectionDownloadTask(MapsActivity parent, LatLng startLatLng, LatLng destLatLng) {
        super(parent);
        this.startLatLng = startLatLng;
        this.destLatLng = destLatLng;
    }

    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service

        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(this.getDirectionsUrl());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Background Task");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        JSONObject jObject;
        try{
            jObject = new JSONObject(result);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            Navigation navigation = new Navigation();
            navigation.setStartAddress(parser.parseStartStreet(jObject));
            navigation.setEndAddress(parser.parseEndStreet(jObject));
            navigation.setTime(parser.parseTime(jObject));
            navigation.setDistance(parser.parseDistance(jObject));
            navigation.setPolylineOptions(parser.parseLatLng(jObject));
            navigation.setLocationFrom(this.startLatLng);
            navigation.setLocationTo(this.destLatLng);
            navigation.setWayPoints(parser.parseWayPoints(jObject));
            this.parent.setNavigation(navigation);
            this.parent.drawMap();
            this.parent.updateUI();

        }catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "DirectionsJSONParser");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }

    }

    public String getDirectionsUrl(){


        // Origin of route
        String str_origin = "origin="+this.startLatLng.latitude+","+this.startLatLng.longitude;

        // Destination of route
        String str_dest = "destination="+this.destLatLng.latitude+","+this.destLatLng.longitude;

        // travel mode
        String str_mode = "mode="+this.parent.getResources().getString(R.string.google_mode);
        // Sensor enabled
        String str_sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+str_mode+"&"+str_sensor;

        parameters = parameters + "&key="+ this.parent.getResources().getString(R.string.google_maps_key);
        //TODO access parameter

        // Output format
        String output = "json";



        // Building the url to the web service
        this.url = this.parent.getResources().getString(R.string.google_url_directions)+output+"?"+parameters;

        return url;
    }

}
