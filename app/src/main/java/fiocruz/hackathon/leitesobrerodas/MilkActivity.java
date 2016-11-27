package fiocruz.hackathon.leitesobrerodas;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import fiocruz.hackathon.leitesobrerodas.utils.CustomList;

public class MilkActivity  extends AppCompatActivity implements View.OnClickListener {

    private final static int CAMERA_RQ = 6969;
    private final static int PERMISSION_RQ = 84;

    ListView     list;
    int          bottlesNum;
    List<String> web = new ArrayList<>();
    List<Bitmap>  images = new ArrayList<>();
    TextView     mActionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk);
        findViewById(R.id.fab).setOnClickListener(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        mActionTitle = (TextView) findViewById(R.id.action_bar_title);
        mActionTitle.setText("Rua de Aquele Lugar, 771");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to save videos in external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
        }

        bottlesNum = 1;

        for(int i = 1; i < bottlesNum; i++) {
            web.add("Frasco #" + i);
        }

        String[] req = web.toArray(new String[web.size()]);
        Bitmap[] imgs = images.toArray(new Bitmap[images.size()]);
        CustomList adapter = new CustomList(MilkActivity.this, req, imgs);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MilkActivity.this, web.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                web.clear();
                bottlesNum = bottlesNum + 1;

                for(int i = 1; i < bottlesNum; i++) web.add("Frasco #" + i);

                String[] req = web.toArray(new String[web.size()]);
                CustomList adapter = new CustomList(MilkActivity.this, req);
                list = (ListView)findViewById(R.id.list);
                list.setAdapter(adapter);
            }
        });*/
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onClick(View view) {

        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "MaterialCamera");
            saveDir.mkdirs();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(true)
                .allowRetry(true)
                .allowRetry(true)
                .autoSubmit(false)
                .labelConfirm(R.string.mcam_use_video);

        if (view.getId() == R.id.fab)
            materialCamera
                    .stillShot() // launches the Camera in stillshot mode
                    .labelConfirm(R.string.mcam_use_stillshot);
        materialCamera.start(CAMERA_RQ);
    }
    private String readableFileSize(long size) {
        if (size <= 0) return size + " B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String fileSize(File file) {
        return readableFileSize(file.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_RQ) {
            if (resultCode == RESULT_OK) {
                final File file = new File(data.getData().getPath());
                web.clear();
                bottlesNum = bottlesNum + 1;
                images.add(BitmapFactory.decodeFile(file.getAbsolutePath()));

                web.add("Temperatura atual");

                for(int i = 2; i < bottlesNum; i++) web.add("Frasco #" + (i - 1));

                String[] req = web.toArray(new String[web.size()]);
                Bitmap[] imgs = images.toArray(new Bitmap[images.size()]);
                CustomList adapter = new CustomList(MilkActivity.this, req, imgs);
                list = (ListView)findViewById(R.id.list);
                list.setAdapter(adapter);
            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // Sample was denied WRITE_EXTERNAL_STORAGE permission
            Toast.makeText(this, "Videos will be saved in a cache directory instead of an external storage directory since permission was denied.", Toast.LENGTH_LONG).show();
        }
    }

}
