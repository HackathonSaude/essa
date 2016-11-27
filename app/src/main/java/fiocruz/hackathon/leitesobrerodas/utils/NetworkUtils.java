package fiocruz.hackathon.leitesobrerodas.utils;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {
    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private String name;
    private String CPF;

    public String post(String url, String name, String CPF) throws IOException {
        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=---011000010111000001101001");
        RequestBody body = RequestBody.create(mediaType, "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"nome\"\r\n\r\n" +name+"\r\n-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"cpf\"\r\n\r\n"+CPF+"\r\n-----011000010111000001101001--");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "c7f15a9c-fb86-09df-d2f4-391860116b66")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
