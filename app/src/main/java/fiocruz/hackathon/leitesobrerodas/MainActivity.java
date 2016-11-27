package fiocruz.hackathon.leitesobrerodas;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fiocruz.hackathon.leitesobrerodas.utils.GetDirectionsAsync;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {

    private GoogleMap mMap;
    private float currentZoom = 10;
    Polyline line;
    GetDirectionsAsync getDir;
    LatLng origin = new LatLng(-22.90842, -43.23495);
    List<LatLng> points = new ArrayList<LatLng>();
    List<LatLng> destination = new ArrayList<LatLng>();
    List<LatLng> waypoints = new ArrayList<LatLng>();
    CircleOptions circleOptions;

    TextView mActionTitle;
    Typeface custom_font;
    Locale BRAZIL;
    SimpleDateFormat sdf;
    Date d;
    String dayOfTheWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BRAZIL = new Locale("pt","BR");
        sdf = new SimpleDateFormat("EEEE", BRAZIL);
        d = new Date();
        dayOfTheWeek = sdf.format(d);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        mActionTitle = (TextView) findViewById(R.id.action_bar_title);
        dayOfTheWeek = dayOfTheWeek.substring(0,1).toUpperCase() + dayOfTheWeek.substring(1);
        mActionTitle.setText(dayOfTheWeek);


        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        waypoints.add(new LatLng(-22.91545, -43.20422));
        waypoints.add(new LatLng(-22.92620, -43.19667));
        waypoints.add(new LatLng(-22.93727, -43.19083));
        waypoints.add(new LatLng(-22.95244, -43.19289));

        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat.otf");
        mActionTitle.setTypeface(custom_font);
    }

    public double dist(LatLng start, LatLng end) {
        return Math.sqrt(Math.pow((end.latitude - start.latitude), 2) + Math.pow((end.longitude - start.longitude), 2));
    }

    public void optimizeRoute(List<LatLng> list, LatLng start) {
        LatLng _origin = new LatLng(Integer.MAX_VALUE, Integer.MAX_VALUE);
        LatLng _dest = new LatLng(start.latitude, start.longitude);
        for(int i = 0; i < list.size(); i++) {
            if (dist(start, _origin) < dist(start, list.get(i))) {
                points.add(list.get(i));
            } else {
                _origin = list.get(i);
            }
            if (dist(start, _dest) > dist(start, list.get(i))) {
                points.add(list.get(i));
            } else {
                _dest = list.get(i);
            }
        }
        destination.add(_origin);
        destination.add(_dest);
        drawCircle(_origin);
    }

    private void drawCircle(LatLng point){
        circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(10 * (1 / currentZoom * 50));
        circleOptions.strokeColor(Color.WHITE);
        circleOptions.fillColor(Color.parseColor("#26A69A"));
        circleOptions.strokeWidth(5);
        circleOptions.zIndex(1);
        mMap.addCircle(circleOptions);
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(id);

            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public Marker addMarker(GoogleMap googleMap, double lat, double lon, String title, String snippet) {
        Marker mark = googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(lat, lon))
            .anchor(0.5f, 1f)
            .title(title)
            .snippet(snippet)
            .icon(getBitmapDescriptor(R.drawable.ir_marker))
            /*.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))*/);

        return mark;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-22.91545, -43.20422), 15));
        requestDirection();


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition pos) {
            if (pos.zoom != currentZoom){
                currentZoom = pos.zoom;
                //drawCircle(destination.get(0));
            }
            }
        });

        final Marker temp1 = addMarker(mMap, -22.95244, -43.19289, "Joana Banana", "Av. da Suposição, 493 · (21) 98445-3707");
        final Marker temp2 = addMarker(mMap, -22.92620, -43.19667, "Giulia M. Estrela", "Rua de Aquele Lugar, 771 · (21) 99896-7727");
        final Marker temp3 = addMarker(mMap, -22.93727, -43.19083, "Adriana Melo", "Rua do Exemplo, 31 · (21) 99804-4102");

        mMap.setOnInfoWindowClickListener(
            new GoogleMap.OnInfoWindowClickListener(){
                public void onInfoWindowClick(Marker marker){
                    String phone = "55" + marker.getSnippet().split(" · ")[1].replaceAll("[^0-9]","");
                    if (marker.getTitle().indexOf('.') >= 0) {
                        Intent i = new Intent(MainActivity.this, MilkActivity.class);
                        startActivity(i);
                        temp2.remove();
                    } else {
                        Intent sendIntent = new Intent("android.intent.action.MAIN").setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(phone) + "@s.whatsapp.net");

                        startActivity(sendIntent);
                    }
                }
            }
        );
    }

    public void requestDirection() {
        optimizeRoute(waypoints, origin);
        getDir = (GetDirectionsAsync) new GetDirectionsAsync(line, mMap).execute(destination, points);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, R.color.colorAccent));

            for(int i = 0; i < waypoints.size(); i++) {
                drawCircle(waypoints.get(i));
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
    }
}