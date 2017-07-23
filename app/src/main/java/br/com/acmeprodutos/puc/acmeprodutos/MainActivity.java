package br.com.acmeprodutos.puc.acmeprodutos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Spinner spnCliente;
    private List<String> nomesClientes = new ArrayList<String>();

    APIService service;

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

        //Inicia o retrofit
        Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(APIService.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
        service = retrofit.create(APIService.class);

    }

    public void salvarProduto(){

        Produto produto = new Produto();
        produto.setNome("Teste");
        produto.setCliente("coyote");

        //Inserir via Retrofit
        Call<Produto> call = service.createProduto(produto);

        call.enqueue(new Callback<Produto>() {
            @Override
            public void onResponse(Call<Produto> call, Response<Produto> response) {

                if(response.isSuccessful()){

                    Toast.makeText(getBaseContext(), "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    //chamar o listar para atualizar o ListView

                }else{
                    Toast.makeText(getBaseContext(), "Houve um erro na comunicação!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Produto> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Houve um erro na comunicação!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void excluirProduto(Produto produto){

        //Excluir produto via Retrofit
        Call<ResponseBody> call = service.deleteProduto(produto.getID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()){

                    Toast.makeText(getBaseContext(), "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    //chamar o listar para atualizar o ListView

                }else{
                    Toast.makeText(getBaseContext(), "Houve um erro na comunicação!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Houve um erro na comunicação!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
