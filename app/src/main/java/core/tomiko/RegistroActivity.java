package core.tomiko;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static core.tomiko.publico.Funciones.primero;
import static core.tomiko.publico.Funciones.segundo;

/**
 * By: El Bryant
 */

public class RegistroActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    @BindView(R.id.btnRegistrarme) Button btnRegistrarme;
    @BindView(R.id.tietDni) TextInputEditText tietDni;
    @BindView(R.id.tietApellidos) TextInputEditText tietApellidos;
    @BindView(R.id.tietNombres) TextInputEditText tietNombres;
    @BindView(R.id.tietCelular) TextInputEditText tietCelular;
    @BindView(R.id.tietCorreo) TextInputEditText tietCorreo;
    @BindView(R.id.tietDireccion) TextInputEditText tietDireccion;
    @BindView(R.id.titeUsuario) TextInputEditText tietUsuario;
    @BindView(R.id.tietClave) TextInputEditText tietClave;
    @BindView(R.id.tilDni) TextInputLayout tilDni;
    @BindView(R.id.tilApellidos) TextInputLayout tilApellidos;
    @BindView(R.id.tilNombres) TextInputLayout tilNombres;
    @BindView(R.id.tilCelular) TextInputLayout tilCelular;
    @BindView(R.id.tilCorreo) TextInputLayout tilCorreo;
    @BindView(R.id.tilDireccion) TextInputLayout tilDireccion;
    @BindView(R.id.tilUsuario) TextInputLayout tilUsuario;
    @BindView(R.id.tilClave) TextInputLayout tilClave;
    @BindView(R.id.flayCargando) FrameLayout flayCargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ButterKnife.bind(this);
        tietDni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (tietDni.getText().length() == 8) {
                    flayCargando.setVisibility(View.VISIBLE);
                    validarDni();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        tietApellidos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                tietApellidos.setEnabled(false);
                tietNombres.setEnabled(false);
                flayCargando.setVisibility(View.GONE);
                tietCelular.requestFocus();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistroActivity.this, AccesoActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btnRegistrarme) void registro() {
        Log.i("registro", "RegistroActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("https://tomikocourier.com/wsApp/registro_usuario.php?dni=" + tietDni.getText().toString() + "&apellidos="
                        + tietApellidos.getText().toString() + "&nombres=" + tietNombres.getText().toString() + "&celular=" + tietCelular.getText().toString() + "&correo="
                        + tietCorreo.getText().toString() + "&direccion=" + tietDireccion.getText().toString() + "&tipo_usuario=1" + "&usuario=" + tietUsuario.getText().toString() + "&clave="
                        + tietClave.getText().toString());
                Log.i("registro", result);
                runOnUiThread(() -> {
                    int r = segundo(result);
                    if (r > 0) {
                        Toast.makeText(RegistroActivity.this, "Gracias por su registro", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };
        tr.start();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.tietDni:
                if (b) {
                    tilDni.setHelperText("Tu número de DNI");
                } else {
                    tilDni.setHelperText(" ");
                    if (tietDni.getText().toString().equals("")) {
                        tilDni.setError("DNI no puede quedar vacío");
                    } else if (tietDni.getText().toString().length() < 8) {
                        tilDni.setError("DNI debe contener 8 caracteres");
                    }
                }
                break;
            case R.id.tietApellidos:
                if (b) {
                    tilApellidos.setHelperText("Tus apellidos");
                } else {
                    tilApellidos.setHelperText(" ");
                    if (tietApellidos.getText().toString().equals("")) {
                        tilApellidos.setError("Sus apellidos son necesarios");
                    }
                }
                break;
            case R.id.tietNombres:
                if (tietNombres.isFocused()) {
                    tilNombres.setHelperText("Tus nombres");
                } else {
                    tilNombres.setHelperText(" ");
                    if (tietNombres.getText().toString().equals("")) {
                        tilNombres.setError("Sus nombres son necesarios");
                    }
                }
                break;
            case R.id.tietCelular:
                if (tietCelular.isFocused()) {
                    tilCelular.setHelperText("Tu número de celular");
                } else {
                    tilCelular.setHelperText(" ");
                    if (tietCelular.getText().toString().equals("")) {
                        tilCelular.setError("Celular es obligatorio");
                    } else if (tietCelular.getText().toString().length() < 9) {
                        tilCelular.setError("Celular debe contener 9 caracteres");
                    }
                }
                break;
            case R.id.tietCorreo:
                if (tietCorreo.isFocused()) {
                    tilCorreo.setHelperText("Tu correo electrónico Gmail/Hotmail/Outlook");
                } else {
                    tilCorreo.setHelperText(" ");
                    if (tietCorreo.getText().toString().equals("")) {
                        tilCorreo.setError("Necesitamos tu correo electrónico");
                    }
                }
                break;
            case R.id.tietDireccion:
                if (tietDireccion.isFocused()) {
                    tilDireccion.setHelperText("La dirección de tu actual domicilio");
                } else {
                    tilDireccion.setHelperText(" ");
                    if (tietDireccion.getText().toString().equals("")) {
                        tilDireccion.setError("Es necesario para el registro");
                    }
                }
                break;
        }
    }

    public void validarDni() {
        Log.i("validarDni", "RegistroActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("https://bootcampdojo.com/consulta_dni.php?dni=" + tietDni.getText().toString());
                Log.i("validarDni", result);
                runOnUiThread(() -> {
                    int r = segundo(result);
                    if (r > 0) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.length() > 0) {
                                String apellidoPaterno = jsonObject.getString("apellido_paterno");
                                String apellidoMaterno = jsonObject.getString("apellido_materno");
                                String nombres = jsonObject.getString("nombres");
                                tietApellidos.setText(apellidoPaterno + " " + apellidoMaterno);
                                tietNombres.setText(nombres);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        tr.start();
    }
}