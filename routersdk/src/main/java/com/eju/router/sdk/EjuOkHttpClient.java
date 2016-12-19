package com.eju.router.sdk;


import com.eju.router.sdk.exception.EjuException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/*package*/ class EjuOkHttpClient extends EjuHttpClient<Request, Response> {

    private OkHttpClient client;

    EjuOkHttpClient(int timeout) {
        super(timeout);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        client = builder.build();
    }

    /**
     * execute request with OkHttp.
     * @param ejuRequest the request
     * @return the response
     * @throws EjuException
     */
    @Override
    public EjuResponse execute(EjuRequest ejuRequest) throws EjuException {
        Request request = getRequest(ejuRequest);
        Call call = client.newCall(request);
        Response response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new EjuException(e);
        }
        return getResponse(response);
    }

    /**
     * translate {@link EjuRequest} to OkHttpRequest.
     * @param request EJuRequest
     * @return the OkHttpRequest
     */
    @Override
    Request getRequest(EjuRequest request) {
        Request.Builder okHttpRequestBuilder = new Request.Builder();
        okHttpRequestBuilder.url(request.getUrl());
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            okHttpRequestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        okHttpRequestBuilder.header(EjuRequest.CONTENT_TYPE, request.getContentType());
        RequestBody requestBody = null;
        if (request.getBody() != null) {
            requestBody = RequestBody.create(MediaType.parse(request.getContentType()),
                    request.getBody());
        }
        return okHttpRequestBuilder.method(getMethod(request.getMethod()), requestBody).build();
    }

    /**
     * translate OkHttpResponse to {@link EjuResponse}
     * @param response OkHttpResponse
     * @return EjuResponse
     * @throws EjuException
     */
    @Override
    EjuResponse getResponse(Response response) throws EjuException {
        HashMap<String, String> headers = new HashMap<>();
        for (String name : response.headers().names()) {
            headers.put(name, response.header(name));
        }
        byte[] data = null;
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            try {
                data = responseBody.bytes();
            } catch (IOException e) {
                EjuLog.e("Unable to parse response body!", e);
            }
        }
        return new EjuResponse(
                response.code(),
                headers,
                data
        );
    }

    private String getMethod(int method) {
        switch (method) {
            case EjuRequest.METHOD_DELETE:
                return "DELETE";
            case EjuRequest.METHOD_GET:
                return "GET";
            case EjuRequest.METHOD_POST:
                return "POST";
            case EjuRequest.METHOD_PUT:
                return "PUT";
            default:
                return "UNKNOWN";
        }
    }
}
