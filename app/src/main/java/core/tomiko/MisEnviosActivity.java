package core.tomiko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONArray;
import java.util.ArrayList;
import core.tomiko.adapters.MisEnviosAdapter;
import core.tomiko.entity.MisEnvios;
import core.tomiko.publico.PrefUtil;
import static core.tomiko.publico.Funciones.primero;
import static core.tomiko.publico.Funciones.segundo;

public class MisEnviosActivity extends AppCompatActivity {
    ArrayList<MisEnvios> misEnvios;
    PrefUtil prefUtil;
    MisEnviosAdapter misEnviosAdapter;
    RecyclerView rvMisEnvios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_envios);
        initComponents();
        prefUtil = new PrefUtil(this);
        rvMisEnvios = (RecyclerView) findViewById(R.id.rvMisEnvios);
        rvMisEnvios.setLayoutManager(new LinearLayoutManager(this));
        cargarEnvios();
    }

    public void initComponents() {
    }

    public void cargarEnvios() {
        Log.i("cargarEnvios", "MisEnviosActivity");
        misEnvios = new ArrayList<>();
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("https://tomikocourier.com/wsApp/obtener_envios.php?dni=" + prefUtil.getStringValue("dni"));
                Log.i("cargarEnvios", result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = segundo(result);
                        if (r > 0) {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    misEnvios.add(new MisEnvios(jsonArray.getJSONObject(i).getString("id_envio"), jsonArray.getJSONObject(i).getString("direccion_recojo"),
                                            jsonArray.getJSONObject(i).getString("direccion_entrega"), jsonArray.getJSONObject(i).getString("fecha_solicitado"),
                                            jsonArray.getJSONObject(i).getString("fecha_entregado"), jsonArray.getJSONObject(i).getString("nombre")));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        misEnviosAdapter = new MisEnviosAdapter(MisEnviosActivity.this, misEnvios);
                        rvMisEnvios.setAdapter(misEnviosAdapter);
                    }
                });
            }
        };
        tr.start();
    }

    public void onBackPressed() {
        Intent intent = new Intent(MisEnviosActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}