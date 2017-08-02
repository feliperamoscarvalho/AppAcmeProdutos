package br.com.acmeprodutos.puc.acmeprodutos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class MainActivity extends AppCompatActivity {

    private Spinner spnCliente;
    private ImageView imgSalvar;
    private EditText edtProduto;
    private ListView listaProdutos;
    APIService service;

    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Preenche o spinner
        spnCliente = (Spinner) findViewById(R.id.spnCliente);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.clientes_array, android.R.layout.simple_spinner_dropdown_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCliente.setAdapter(adapterSpinner);

        //Exibe o tipoCliente salvo no preferences e desabilita o Spinner
        Preferencias preferencias = new Preferencias(MainActivity.this);
        String tipoCliente = preferencias.getTipoCliente();
        if(tipoCliente != null){
            if(tipoCliente == "coyote"){
                spnCliente.setSelection(1);
            }else if(tipoCliente == "papa-leguas"){
                spnCliente.setSelection(2);
            }
            spnCliente.setEnabled(false);
        }

        imgSalvar = (ImageView) findViewById(R.id.imgSalvar);
        edtProduto = (EditText) findViewById(R.id.edtProduto);

        //Inicia o retrofit
        Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(APIService.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
        service = retrofit.create(APIService.class);

        //Inicia o Hub Notification
        mainActivity = this;
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();

        imgSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtProduto.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Digite o nome do produto!", Toast.LENGTH_SHORT).show();
                }else{
                    salvarProduto();
                    edtProduto.setText("");
                }


            }
        });

        listarProdutos();

    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                TextView helloText = (TextView) findViewById(R.id.text_hello);
                helloText.setText(notificationMessage);
            }
        });
    }

    public void listarProdutos(){

        Call<List<Produto>> call = service.listProdutos();
        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {

                if(response.isSuccessful()){

                    List<Produto> produtos = response.body();
                    final ArrayAdapter<Produto> adapter = new ArrayAdapter<Produto>(MainActivity.this, android.R.layout.simple_list_item_activated_1, produtos);
                    listaProdutos = (ListView) findViewById(R.id.listaProdutos);
                    listaProdutos.setAdapter(adapter);

                    listaProdutos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                            final Produto produtoSelecionado = adapter.getItem(position);

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                            builder.setTitle("Excluir produto");
                            builder.setMessage("Deseja excluir o produto " + produtoSelecionado.getNome() +"?");

                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    excluirProduto(produtoSelecionado);
                                }
                            });

                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            return false;
                        }
                    });


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
                    listarProdutos();

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
                    listarProdutos();

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

    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }
}
