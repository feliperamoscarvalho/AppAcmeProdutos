package br.com.acmeprodutos.puc.acmeprodutos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
    private ImageView imgSalvar;
    private EditText edtProduto;
    private ListView listaProdutos;
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

        imgSalvar = (ImageView) findViewById(R.id.imgSalvar);
        edtProduto = (EditText) findViewById(R.id.edtProduto);

        //Inicia o retrofit
        Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(APIService.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
        service = retrofit.create(APIService.class);

        imgSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarProduto();
                listarProdutos();
            }
        });

        listarProdutos();

    }

    public void listarProdutos(){

        Call<List<Produto>> call = service.listProdutos();
        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {

                if(response.isSuccessful()){

                    List<Produto> produtos = response.body();
                    ArrayAdapter<Produto> adapter = new ArrayAdapter<Produto>(MainActivity.this, android.R.layout.simple_list_item_1, produtos);
                    listaProdutos = (ListView) findViewById(R.id.listaProdutos);
                    listaProdutos.setAdapter(adapter);

                }else{
                    Toast.makeText(getBaseContext(), "Houve um erro na comunicação!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Houve um erro na comunicação!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void salvarProduto(){

        Produto produto = new Produto();
        produto.setNome(edtProduto.getText().toString());
        produto.setCliente(spnCliente.getSelectedItem().toString());

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
