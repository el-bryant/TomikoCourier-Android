package core.tomiko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import core.tomiko.publico.PrefUtil;

/**
 * By: El Bryant
 */

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.cvSolicitarEnvio) CardView cvSolicitarEnvio;
    @BindView(R.id.cvMisEnvios) CardView cvMisEnvios;
    @BindView(R.id.tvCerrar) TextView tvCerrar;
    PrefUtil prefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
    }

    @OnClick(R.id.cvMisEnvios) void misEnvios() {
        Intent intent = new Intent(MainActivity.this, MisEnviosActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.cvSolicitarEnvio) void solicitarEnvio() {
        Intent intent = new Intent(MainActivity.this, SolicitarEnvioActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.tvCerrar) void cerrarSesion() {
        prefUtil.clearAll();
        Intent intent = new Intent(MainActivity.this, AccesoActivity.class);
        startActivity(intent);
        finish();
    }
}