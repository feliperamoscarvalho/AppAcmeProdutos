package br.com.acmeprodutos.puc.acmeprodutos;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    String BASE_URL = "http://localhost/";

    @POST("Produtos")
    Call<Produto> createProduto(@Body Produto produto);

    @DELETE("Produtos/{ID}")
    Call<ResponseBody> deleteProduto (@Path("ID") int ID);

}
