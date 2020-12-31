package core.tomiko;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import core.tomiko.publico.PrefUtil;

public class SplashActivity extends AppCompatActivity {
    AlertDialog alert = null;
    ImageView ivIsotipo, ivLogotipo;
    PrefUtil prefUtil;
    LocationManager locationManager;
    public static int i = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        prefUtil = new PrefUtil(this);
        ivIsotipo = (ImageView) findViewById(R.id.ivIsotipo);
        ivLogotipo = (ImageView) findViewById(R.id.ivLogotipo);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                } else {
                    animar();
                }
            }
        }
    }

    public void animar() {
        ivIsotipo.setVisibility(View.VISIBLE);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivIsotipo, View.ALPHA, 0.0f, 1.0f);
        long animationDuration1 = 2000;
        animator1.setDuration(animationDuration1);
        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1.play(animator1);
        animatorSet1.start();
        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
                if (l < 3500 && l > 2000) {
                    ivLogotipo.setVisibility(View.VISIBLE);
                    Animation animationScale = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.scale);
                    ivLogotipo.startAnimation(animationScale);
                }
            }
            @Override
            public void onFinish() {
                if (prefUtil.getStringValue(PrefUtil.LOGIN_STATUS).equals("1")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, AccesoActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }

    public void AlertNoGps() {
        if (alert == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El GPS está desactivado, ¿desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            CountDownTimer countDownTimer = new CountDownTimer(3000, i) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    i = 1000;
                                }
                                @Override
                                public void onFinish() {
                                    animar();
                                }
                            };
                            countDownTimer.start();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                } else {
                    animar();
                }
            } else {
                Log.i("Permiso denegado", "ubicación");
            }
        }
    }
}