package br.com.acmeprodutos.puc.acmeprodutos;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    String BASE_URL = "http://apiacmecorp.azurewebsites.net/api/";

    @GET("Produtos")
    Call<List<Produto>> listProdutos();

    @POST("Produtos")
    Call<Produto> createProduto(@Body Produto produto);

    @DELETE("Produtos/{ID}")
    Call<ResponseBody> deleteProduto (@Path("ID") int ID);

}
