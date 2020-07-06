package core.tomiko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AccesoActivity extends AppCompatActivity {
    TextView tvRegistro;
    Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        tvRegistro = (TextView) findViewById(R.id.tvRegistro);
        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        tvRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccesoActivity.this, RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccesoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}