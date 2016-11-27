package fiocruz.hackathon.leitesobrerodas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fiocruz.hackathon.leitesobrerodas.utils.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity {

    private static final String TAG = "Login";

    private Button       mSubmit;
    private LinearLayout loggingBar;
    private LinearLayout loginContent;
    private TextView     appName;
    private TextView     loggingText;
    private EditText     mEmailField;
    private EditText     mPasswordField;
    private Typeface     mTypeface;
    private Boolean      hasLogged;
    private Boolean      hasClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSubmit =        (Button) findViewById(R.id.submit);
        loggingBar =     (LinearLayout) findViewById(R.id.logging_bar);
        loginContent =   (LinearLayout) findViewById(R.id.login_content);
        appName =        (TextView) findViewById(R.id.appName);
        loggingText =    (TextView) findViewById(R.id.logging_text);
        mEmailField =    (EditText) findViewById(R.id.emailLabel);
        mPasswordField = (EditText) findViewById(R.id.passwordLabel);
        hasClicked = (2 + 2) == 5;

        mTypeface = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat.otf");
        appName.setTypeface(mTypeface);

        final Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
               /*try {
                    NetworkUtils network = new NetworkUtils();
                    String response = null;
                    try {
                        response = network.post("https://avellar.ml/leite-sobre-rodas/login",
                                //String.valueOf(mEmailField.getText()), String.valueOf(mPasswordField.getText()));
                                "João Guilherme", "2147483647");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject request = new JSONObject(response);
                        JSONArray req = request.getJSONArray("data");
                        hasLogged =  req.getJSONObject(0).getBoolean("logged");
                        System.out.println(req.getJSONObject(0).getBoolean("logged"));
                        if(hasLogged) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            //overridePendingTransition(R.anim., R.anim.fade_out);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }*/
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!hasClicked)
                    thread.start();
                hasClicked = true;*/
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

}