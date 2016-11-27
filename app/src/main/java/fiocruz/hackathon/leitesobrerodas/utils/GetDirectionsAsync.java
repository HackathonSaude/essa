package fiocruz.hackathon.leitesobrerodas.utils;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fiocruz.hackathon.leitesobrerodas.MainActivity;
import fiocruz.hackathon.leitesobrerodas.R;

public class GetDirectionsAsync extends AsyncTask<List<LatLng>, Void, List<LatLng>> {

        JSONParser jsonParser;
        String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json";
        Polyline line;
        GoogleMap mMap;

        public GetDirectionsAsync(Polyline line, GoogleMap mMap) {
            this.line = line;
            this.mMap = mMap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        private String formatWaypoints(List<LatLng> list) {
            String waypoints = "optimize:true";
            for (int i = 0; i < list.size(); i++) {
                LatLng temp = list.get(i);
                waypoints += "|" + temp.latitude + "," + temp.longitude;
            }
            return waypoints;
        }
        @Override
        protected List<LatLng> doInBackground(List<LatLng>... params) {
            HashMap<String, String> points = new HashMap<>();
            LatLng start = params[0].get(0);
            LatLng end = params[0].get(params[0].size() - 1);

            if(!(params[1].size() == 0)) points.put("waypoints", formatWaypoints(params[1]));

            points.put("origin", start.latitude + "," + start.longitude);
            points.put("destination", end.latitude + "," + end.longitude);

            jsonParser = new JSONParser();

            JSONObject obj = jsonParser.makeHttpRequest(DIRECTIONS_URL, "GET", points, true);

            if (obj == null) return null;

            try {
                List<LatLng> list = null;

                JSONArray routeArray = obj.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                list = decodePoly(encodedString);
                return list;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public void onPostExecute(List<LatLng> pointsList) {

            if (pointsList == null) return;

            if (line != null){
                line.remove();
            }

            PolylineOptions options = new PolylineOptions().width(15).color(Color.parseColor("#e74c3c")).geodesic(true);
            for (int i = 0; i < pointsList.size(); i++) {
                LatLng point = pointsList.get(i);
                options.add(point);
            }
            line = mMap.addPolyline(options);
        }

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