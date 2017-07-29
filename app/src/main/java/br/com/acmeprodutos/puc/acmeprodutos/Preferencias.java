package br.com.acmeprodutos.puc.acmeprodutos;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Felipe on 29/07/2017.
 */

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "acmeprodutos.preferencias";
    private final int MODE = 0;
    private SharedPreferences.Editor editor;
    private final String CHAVE_CLIENTE = "identificadorCliente";

    public Preferencias(Context contextoParametro){

        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();

    }

    public void salvarDados(String identificadorCliente){

        editor.putString(CHAVE_CLIENTE, identificadorCliente);
        editor.commit();

    }

    public String getIdentificador(){

        return preferences.getString(CHAVE_CLIENTE, null);

    }
}
