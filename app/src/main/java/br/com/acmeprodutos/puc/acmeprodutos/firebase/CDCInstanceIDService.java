package br.com.acmeprodutos.puc.acmeprodutos.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.acmeprodutos.puc.acmeprodutos.Preferencias;

/**
 * Created by Felipe on 27/07/2017.
 */

public class CDCInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token da App", token);
        Preferencias preferencias = new Preferencias(CDCInstanceIDService.this);
        preferencias.salvarIdentificador(token);

    }

    public static String getToken(){
        return FirebaseInstanceId.getInstance().getToken();
    }
}
