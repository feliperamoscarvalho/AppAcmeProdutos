package br.com.acmeprodutos.puc.acmeprodutos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private Spinner spnClienteUsuario;
    private Button btnCadastrarUsuario;
    private EditText edtNomeUsuario;
    APIService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
        String tipoCliente = preferencias.getTipoCliente();
        if (tipoCliente != null){

            //Usuário já cadastrado no dispositivo, abre o MainActivity
            Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
            startActivity(intent);

        }else{

            //Usuário não cadastrado no dispositivo

            //Preenche o Spinner
            spnClienteUsuario = (Spinner) findViewById(R.id.spnClienteUsuario);
            ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.clientes_array, android.R.layout.simple_spinner_dropdown_item);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnClienteUsuario.setAdapter(adapterSpinner);

            //Inicia o retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(APIService.class);

            edtNomeUsuario = (EditText) findViewById(R.id.edtNomeUsuario);

            btnCadastrarUsuario = (Button)findViewById(R.id.btnCadastrarUsuario);
            btnCadastrarUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (validarDados()) {

                        //Cadastrar usuário na API REST
                        salvarUsuario();

                        //Abre o MainActivity
                        Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                }
            });

        }

    }

    public void salvarUsuario(){

        final Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
        final String token = preferencias.getIdentificador();

        Usuario usuario = new Usuario();
        usuario.setCliente(spnClienteUsuario.getSelectedItem().toString());
        usuario.setNome(edtNomeUsuario.getText().toString());
        usuario.setRegistration_id(token);

        //Inserir via Retrofit
        Call<Usuario> call = service.createUsuario(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if(response.isSuccessful()){

                    //Salva tipo do cliente do usuário no SharedPreferences
                    preferencias.salvarTipoCliente(spnClienteUsuario.getSelectedItem().toString());

                }else{
                    Toast.makeText(getBaseContext(), "Houve um erro na comunicação!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Houve um erro na comunicação!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean validarDados(){

        boolean dadosValidos = true;

        if (edtNomeUsuario.getText().toString().equals("")){

            Toast.makeText(getBaseContext(), "Digite o nome do usuário!", Toast.LENGTH_SHORT).show();
            dadosValidos = false;

        }

        if (spnClienteUsuario.getSelectedItem().toString().equals("")){

            Toast.makeText(getBaseContext(), "Selecione o cliente!", Toast.LENGTH_SHORT).show();
            dadosValidos = false;

        }

        return dadosValidos;

    }
}
