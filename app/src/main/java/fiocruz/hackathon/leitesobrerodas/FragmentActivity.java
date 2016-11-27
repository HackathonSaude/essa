package fiocruz.hackathon.leitesobrerodas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Intent intent = getIntent();
        boolean support = intent.getBooleanExtra("support", false);

        if (savedInstanceState == null) {
                getFragmentManager().beginTransaction().add(R.id.container, DemoFragment.getInstance()).commit();
        }
    }
}