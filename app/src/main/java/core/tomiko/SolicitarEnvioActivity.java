package core.tomiko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import core.tomiko.publico.PrefUtil;
import static core.tomiko.publico.Funciones.primero;
import static core.tomiko.publico.Funciones.segundo;

/**
 * By: El Bryant
 */

public class SolicitarEnvioActivity extends AppCompatActivity {
    DatabaseReference pedido;
    PrefUtil prefUtil;
    @BindView(R.id.clayPaquete) ConstraintLayout clayPaquete;
    @BindView(R.id.rbtSobre) RadioButton rbtSobre;
    @BindView(R.id.rbtPaquete) RadioButton rbtPaquete;
    @BindView(R.id.rgSobrePaquete) RadioGroup rgSobrePaquete;
    @BindView(R.id.tilDireccionRecojo) TextInputLayout tilDireccionRecojo;
    @BindView(R.id.tilDireccionEntrega) TextInputLayout tilDireccionEntrega;
    @BindView(R.id.tilAlto) TextInputLayout tilAlto;
    @BindView(R.id.tilAncho) TextInputLayout tilAncho;
    @BindView(R.id.tilLargo) TextInputLayout tilLargo;
    @BindView(R.id.tilPeso) TextInputLayout tilPeso;
    @BindView(R.id.tietAlto) TextInputEditText tietAlto;
    @BindView(R.id.tietAncho) TextInputEditText tietAncho;
    @BindView(R.id.tietLargo) TextInputEditText tietLargo;
    @BindView(R.id.tietPeso) TextInputEditText tietPeso;
    public static TextView tvLatitudRecojo, tvLongitudRecojo, tvLatitudEntrega, tvLongitudEntrega, tvPrecio;
    public static Button btnRealizarSolicitud;
    public static TextInputEditText tietDireccionRecojo;
    public static TextInputEditText tietDireccionEntrega;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_envio);
        prefUtil = new PrefUtil(this);
        ButterKnife.bind(this);
        btnRealizarSolicitud = (Button) findViewById(R.id.btnRealizarSolicitud);
        tietDireccionRecojo = (TextInputEditText) findViewById(R.id.tietDireccionRecojo);
        tietDireccionEntrega = (TextInputEditText) findViewById(R.id.tietDireccionEntrega);
        tvLatitudEntrega = (TextView) findViewById(R.id.tvLatitudEntrega);
        tvLatitudRecojo = (TextView) findViewById(R.id.tvLatitudRecojo);
        tvLongitudEntrega = (TextView) findViewById(R.id.tvLongitudEntrega);
        tvLongitudRecojo = (TextView) findViewById(R.id.tvLongitudRecojo);
        tvPrecio = (TextView) findViewById(R.id.tvPrecio);
        tietDireccionEntrega.setOnFocusChangeListener((view, b) -> {
            if (b) {
                Intent intent = new Intent(SolicitarEnvioActivity.this, UbicacionesActivity.class);
                intent.putExtra("ubicacion", "entrega");
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            }
        });
        tietDireccionRecojo.setOnFocusChangeListener((view, b) -> {
            if (b) {
                Intent intent = new Intent(SolicitarEnvioActivity.this, UbicacionesActivity.class);
                intent.putExtra("ubicacion", "recojo");
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.btnRealizarSolicitud) void realizarSolicitud() {
        final String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.i("realizarSolicitud", "SolicitarEnvioActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("https://tomikocourier.com/wsApp/registrar_envio.php?direccion_recojo="
                        + tietDireccionRecojo.getText().toString() + "&latitud_recojo=" + tvLatitudRecojo.getText().toString() + "&longitud_recojo=" + tvLongitudRecojo.getText().toString()
                        + "&direccion_entrega=" + tietDireccionEntrega.getText().toString() + "&latitud_entrega=" + tvLatitudEntrega.getText().toString() + "&longitud_entrega="
                        + tvLongitudEntrega.getText().toString() + "&fecha_solicitado=" + fecha + "&alto=" + tietAlto.getText().toString() + "&ancho=" + tietAncho.getText().toString()
                        + "&largo=" + tietLargo.getText().toString() + "&peso=" + tietPeso.getText().toString() + "&precio=" + tvPrecio.getText().toString() + "&dni_cliente="
                        + prefUtil.getStringValue("dni") + "&id_estado_envio=1");
                Log.i("realizarSolicitud", result);
                runOnUiThread(() -> {
                    int r = segundo(result);
                    if (r > 0) {
                        pedido = FirebaseDatabase.getInstance().getReference().child("pedidos").child(prefUtil.getStringValue("dni"));
                        Map<String, Object> datos = new HashMap<>();
                        datos.put("direccion_recojo", tietDireccionRecojo.getText().toString());
                        datos.put("latitud_recojo", tvLatitudRecojo.getText().toString());
                        datos.put("longitud_recojo", tvLongitudRecojo.getText().toString());
                        datos.put("direccion_entrega", tietDireccionEntrega.getText().toString());
                        datos.put("latitud_entrega", tvLatitudEntrega.getText().toString());
                        datos.put("longitud_entrega", tvLongitudEntrega.getText().toString());
                        datos.put("fecha_solicitado", fecha);
                        datos.put("alto", tietAlto.getText().toString());
                        datos.put("ancho", tietAncho.getText().toString());
                        datos.put("largo", tietLargo.getText().toString());
                        datos.put("peso", tietPeso.getText().toString());
                        datos.put("precio", tvPrecio.getText().toString());
                        datos.put("dni_cliente", prefUtil.getStringValue("dni"));
                        datos.put("estado", "SOLICITADO");
                        pedido.setValue(datos);
                        Toast.makeText(SolicitarEnvioActivity.this, "Envío solicitado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SolicitarEnvioActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };
        tr.start();
    }

    @OnClick(R.id.rbtSobre) void sobre() {
        clayPaquete.setVisibility(View.GONE);
    }

    @OnClick(R.id.rbtPaquete) void paquete() {
        clayPaquete.setVisibility(View.VISIBLE);
    }

    public void onBackPressed() {
        Intent intent = new Intent(SolicitarEnvioActivity.this, MainActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }
}