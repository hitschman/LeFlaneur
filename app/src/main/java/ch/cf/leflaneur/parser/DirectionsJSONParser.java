package ch.cf.leflaneur.parser;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.cf.leflaneur.enums.Direction;
import ch.cf.leflaneur.enums.Maneuver2Direction;
import ch.cf.leflaneur.model.WayPoint;

/**
 * Created by christianfallegger on 01.02.17.
 */
public class DirectionsJSONParser {

    private static final String TAG = DirectionsJSONParser.class.getSimpleName();

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parseLatLng(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseLatLng:JSONException");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }catch (Exception e){
            Log.e(TAG, "parseLatLng:Exception");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }
        return routes;
    }

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<WayPoint> parseWayPoints(JSONObject jObject){

        List<WayPoint> wps = new ArrayList<WayPoint>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");


                    /** Traversing all steps */
                    WayPoint wp = new WayPoint();
                    for(int k=0;k<jSteps.length();k++){
                        wp = new WayPoint();
                        wp.setPolyline((String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points"));
                        wp.setPolyLinePoints(decodePoly(wp.getPolyline()));
                        wp.setDuration((int)((JSONObject)((JSONObject)jSteps.get(k)).get("duration")).get("value"));
                        wp.setDistance((int)((JSONObject)((JSONObject)jSteps.get(k)).get("distance")).get("value"));
                        wp.setTravelMode((String) ((JSONObject)jSteps.get(k)).get("travel_mode"));
                        wp.setInstructionHtml((String) ((JSONObject)jSteps.get(k)).get("html_instructions"));
                        if (((JSONObject)jSteps.get(k)).has("maneuver")) {
                            wp.setManeuver((String) ((JSONObject) jSteps.get(k)).get("maneuver"));
                        }else if (k == 0){
                            wp.setManeuver("Start");
                        }else {
                            wp.setManeuver("Continue");
                        }
                        wp.setLatLng(new LatLng(
                                ((double)((JSONObject)((JSONObject)jSteps.get(k)).get("start_location")).get("lat")),
                                ((double)((JSONObject)((JSONObject)jSteps.get(k)).get("start_location")).get("lng"))
                        ));
                        Location location = new Location("GoogleDirection");
                        location.setLatitude(wp.getLatLng().latitude);
                        location.setLongitude(wp.getLatLng().longitude);
                        wp.setLocation(location);
                        wps.add(wp);
                        if(k+1 == jSteps.length()){
                            wp = new WayPoint(wp);
                            wp.setDistance(0);
                            wp.setDuration(0);
                            wp.setManeuver("stop");
                            wp.setDirection(Direction.STOP);
                            wp.setInstructionHtml("<b>You reached your destination.</b>");
                            wp.setLatLng(new LatLng(
                                    ((double)((JSONObject)((JSONObject)jSteps.get(k)).get("end_location")).get("lat")),
                                    ((double)((JSONObject)((JSONObject)jSteps.get(k)).get("end_location")).get("lng"))
                            ));
                            Location locationLast = new Location("GoogleDirection");
                            locationLast.setLatitude(wp.getLatLng().latitude);
                            locationLast.setLongitude(wp.getLatLng().longitude);
                            wp.setLocation(locationLast);
                            wps.add(wp);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:JSONException");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:Exception");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }
        return wps;
    }

    public int parseDistance(JSONObject jObject){

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        int distance = 0;

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    distance = (int) ((JSONObject) ((JSONObject) jLegs.get(j)).get("distance")).get("value");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:JSONException");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:Exception");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }
        return distance;
    }

    public int parseTime(JSONObject jObject){

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        int duration = 0;

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    duration = (int) ((JSONObject) ((JSONObject) jLegs.get(j)).get("duration")).get("value");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:JSONException");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:Exception");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }
        return Math.round(duration/60);
    }

    public String parseStartStreet(JSONObject jObject){

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        String startAddress = "";

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    startAddress = (String) ((JSONObject) jLegs.get(j)).get("start_address");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:JSONException");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:Exception");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }
        return startAddress;
    }

    public String parseEndStreet(JSONObject jObject){

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        String endAddress = "";

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    endAddress = (String) ((JSONObject) jLegs.get(j)).get("end_address");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:JSONException");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "parseWayPoints:Exception");
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getStackTrace().toString());
        }
        return endAddress;
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}