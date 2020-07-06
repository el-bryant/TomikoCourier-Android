package core.tomiko;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import static core.tomiko.publico.Funciones.primero;
import static core.tomiko.publico.Funciones.segundo;

/**
 * By: El Bryant
 */

public class RegistroActivity extends AppCompatActivity {
    EditText etDni, etApellidos, etNombres, etCelular, etCorreo, etDireccion;
    Button btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        etDni = (EditText) findViewById(R.id.etDni);
        etApellidos = (EditText) findViewById(R.id.etApellidos);
        etNombres = (EditText) findViewById(R.id.etNombres);
        etCelular = (EditText) findViewById(R.id.etCelular);
        etCorreo = (EditText) findViewById(R.id.etCorreo);
        etDireccion = (EditText) findViewById(R.id.etDireccion);
        btnRegistro = (Button) findViewById(R.id.btnRegistrar);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registro();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistroActivity.this, AccesoActivity.class);
        startActivity(intent);
        finish();
    }

    public void registro() {
        Log.i("registro", "RegistroActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("https://bootcampdojo.com/tomiko/registro_usuario.php?dni="
                        + etDni.getText().toString() + "&apellidos=" + etApellidos.getText().toString() + "&nombres="
                        + etNombres.getText().toString() + "&celular=" + etCelular.getText().toString() + "&correo="
                        + etCorreo.getText().toString() + "&direccion=" + etDireccion.getText().toString());
                Log.i("registro", result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = segundo(result);
                        if (r > 0) {
                            Toast.makeText(RegistroActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        };
        tr.start();
    }
}