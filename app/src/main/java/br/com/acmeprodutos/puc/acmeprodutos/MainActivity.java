package br.com.acmeprodutos.puc.acmeprodutos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spnCliente;
    private List<String> nomesClientes = new ArrayList<String>();

    // criando o Array de String
    private static final String[] opcoes = { "coyote", "papa-leguas"};
    ArrayAdapter<String> arrayOpcoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cria o spinner
        arrayOpcoes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, opcoes);
        spnCliente = (Spinner) findViewById(R.id.spnCliente);
        spnCliente.setAdapter(arrayOpcoes);

    }
}
