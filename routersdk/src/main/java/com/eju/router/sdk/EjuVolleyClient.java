package com.eju.router.sdk;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;
import com.eju.router.sdk.exception.EjuException;
import com.eju.router.sdk.exception.EjuTimeoutException;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/*package*/ class EjuVolleyClient extends
        EjuHttpClient<Request<NetworkResponse>, Request<NetworkResponse>> {

    private RequestFuture<NetworkResponse> future = RequestFuture.newFuture();
    private RequestQueue requestQueue;

    EjuVolleyClient(int timeout) {
        super(timeout);
//        requestQueue = Volley.newRequestQueue(EjuSSO.getApplicationContext());

    }

    @Override
    public EjuResponse execute(EjuRequest ejuRequest) throws EjuException {
        Request<NetworkResponse> req = getRequest(ejuRequest);
        requestQueue.add(req);
        return getResponse(req);
    }

    @Override
    Request<NetworkResponse> getRequest(final EjuRequest request) {
        return new Request<NetworkResponse>(
                getMethod(request.getMethod()),
                request.getUrl(),
                future
        ) {
            @Override
            protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse networkResponse) {
                return Response.success(networkResponse, HttpHeaderParser.parseCacheHeaders(networkResponse));
            }

            @Override
            protected void deliverResponse(NetworkResponse networkResponse) {
                future.onResponse(networkResponse);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = request.getHeaders();
                if (headers != null && !headers.containsKey(EjuRequest.CONTENT_TYPE)) {
                    headers.put(EjuRequest.CONTENT_TYPE, request.getContentType());
                }
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return request.getContentType();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return request.getBody();
            }
        };
    }

    @Override
    EjuResponse getResponse(Request<NetworkResponse> networkResponseRequest) throws EjuException {
        NetworkResponse networkResponse;
        try {
            networkResponse = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new EjuTimeoutException();
        } catch (Exception e) {
            if (e.getCause() instanceof VolleyError) {
                VolleyError volleyError = (VolleyError) e.getCause();
                if (volleyError.networkResponse != null) {
                    networkResponse = volleyError.networkResponse;
                } else {
                    throw new EjuException(e);
                }
            } else {
                throw new EjuException(e);
            }
        }
        return new EjuResponse(
                networkResponse.statusCode,
                networkResponse.headers,
                networkResponse.data
        );
    }

    protected int getMethod(int method) {
        switch (method) {
            case EjuRequest.METHOD_DELETE:
                return Request.Method.DELETE;
            case EjuRequest.METHOD_GET:
                return Request.Method.GET;
            case EjuRequest.METHOD_POST:
                return Request.Method.POST;
            default:
                return Request.Method.PUT;
        }
    }

}
