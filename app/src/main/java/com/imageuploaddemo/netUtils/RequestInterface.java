package com.imageuploaddemo.netUtils;


import org.json.JSONArray;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RequestInterface {


    // todo Regstration user
    @Multipart
    @POST("demo.php?&user_type=android")
    Call<ResponseBody> RegUser(@Query("uid") String uid,
                               @Part MultipartBody.Part file
    );

}
