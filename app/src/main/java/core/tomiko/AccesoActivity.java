package core.tomiko;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import core.tomiko.publico.PrefUtil;
import static core.tomiko.publico.Funciones.primero;
import static core.tomiko.publico.Funciones.segundo;

/**
 * By: El Bryant
 */

public class AccesoActivity extends AppCompatActivity {
    @BindView(R.id.btnIngresar) Button btnIngresar;
    @BindView(R.id.etClave) EditText etClave;
    @BindView(R.id.etUsuario) EditText etUsuario;
    @BindView(R.id.flayCargando) FrameLayout flayCargando;
    @BindView(R.id.tvRegistro) TextView tvRegistro;
    PrefUtil prefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
    }

    @OnClick(R.id.tvRegistro) void registro() {
        Intent intent = new Intent(AccesoActivity.this, RegistroActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btnIngresar) void ingresar() {
        flayCargando.setVisibility(View.VISIBLE);
        acceder();
    }

    public void acceder() {
        Log.i("acceder", "AccesoActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("https://tomikocourier.com/wsApp/acceso.php?usuario=" + etUsuario.getText().toString() + "&clave=" + etClave.getText().toString()
                        + "&tipo_usuario=1");
                Log.i("acceder", result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = segundo(result);
                        if (r > 0) {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                if (jsonArray.length() > 0) {
                                    prefUtil.saveGenericValue("nombres", jsonArray.getJSONObject(0).getString("nombres"));
                                    prefUtil.saveGenericValue("apellidos", jsonArray.getJSONObject(0).getString("apellidos"));
                                    prefUtil.saveGenericValue(PrefUtil.LOGIN_STATUS, "1");
                                    prefUtil.saveGenericValue("dni", etUsuario.getText().toString());
                                    Toast.makeText(AccesoActivity.this, "Bienvenido de nuevo", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AccesoActivity.this, MainActivity.class);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    flayCargando.setVisibility(View.GONE);
                                    Toast.makeText(AccesoActivity.this, "Usuario incorrecto, por favor regístrese", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        };
        tr.start();
    }
}