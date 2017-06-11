package ch.cf.leflaneur.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by christianfallegger on 21.02.17.
 */

public class PlaceJSONParser {

    public List<HashMap<String,String>> parse(JSONObject jObject){

        JSONArray jPlaces = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            jPlaces = jObject.getJSONArray("predictions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaces(jPlaces);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> place = null;

        /** Taking each place, parses and adds to list object */
        for(int i=0; i<placesCount;i++){
            try {
                /** Call getPlace with place JSON object to parse the place */
                place = getPlace((JSONObject)jPlaces.get(i));
                placesList.add(place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    /** Parsing the Place JSON object */
    private HashMap<String, String> getPlace(JSONObject jPlace){

        HashMap<String, String> place = new HashMap<String, String>();

        String id="";
        String reference="";
        String description="";
        String place_id = "";
        String main_text = "";
        String secondary_text = "";

        try {

            description = jPlace.getString("description");
            id = jPlace.getString("id");
            reference = jPlace.getString("reference");
            place_id = jPlace.getString("place_id");
            if (jPlace.has("structured_formatting")){
                if (jPlace.getJSONObject("structured_formatting").has("main_text")){
                    main_text = jPlace.getJSONObject("structured_formatting").getString("main_text");
                }else{
                    main_text = description;
                }
                if (jPlace.getJSONObject("structured_formatting").has("secondary_text")){
                    secondary_text = jPlace.getJSONObject("structured_formatting").getString("secondary_text");
                }
            }else{
                main_text = description;
            }




            //TODO Main and Secondary Text
            place.put("description", description);
            place.put("_id",id);
            place.put("reference",reference);
            place.put("place_id", place_id);
            place.put("main_text", main_text);
            place.put("secondary_text",secondary_text);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}
