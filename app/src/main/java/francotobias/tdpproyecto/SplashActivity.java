package francotobias.tdpproyecto;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import francotobias.tdpproyecto.BusModel.LineManager;
import francotobias.tdpproyecto.Helpers.DataManager;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;


public class SplashActivity extends Activity {

    private Runnable task = new Runnable() {
        public void run() {
            DataManager.getInstance().update();
            end();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        DataManager.getInstance().startUpdater(getApplicationContext());
        if(!DataManager.getInstance().needUpdate()) end();

        Handler handler = new Handler();
        handler.postDelayed(task, 1500);

    }

    public void end() {
	    LineManager.initLines();
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}


