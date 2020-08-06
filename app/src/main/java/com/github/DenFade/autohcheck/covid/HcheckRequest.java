package com.github.DenFade.autohcheck.covid;

import com.github.DenFade.autohcheck.Utils;
import com.github.DenFade.autohcheck.exception.AuthorizeHcheckException;
import com.github.DenFade.autohcheck.exception.EndHcheckException;
import com.github.DenFade.autohcheck.exception.EnterHcheckException;
import com.github.DenFade.autohcheck.exception.SubmitHcheckException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HcheckRequest {

    private final HcheckClient client;

    private String authorize_key;
    private String schoolName;
    private String studentName;

    private static final String baseURL = "https://eduro.%s.go.kr";
    private static final String initURL = baseURL + "/hcheck/index.jsp";
    private static final String authURL = baseURL + "/stv_cvd_co00_012.do";
    private static final String submitURL = baseURL + "/stv_cvd_co01_000.do";
    private static final String finalURL = baseURL + "/stv_cvd_co02_000.do";

    public HcheckRequest(HcheckClient client){
        this.client = client;
    }

    public String getInitURL(){
        return String.format(initURL, client.getLocalEdu());
    }

    public String getAuthURL() {
        return String.format(authURL, client.getLocalEdu());
    }

    public String getSubmitURL(){
        return String.format(submitURL, client.getLocalEdu());
    }

    public String getFinalURL() {
        return String.format(finalURL, client.getLocalEdu());
    }

    public HcheckRequest enter() throws EnterHcheckException {

        OkHttpClient ohc = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getInitURL())
                .get()
                .build();
        try{
            Response res = ohc.newCall(request).execute();
            String cookie = Utils.cookieParser(res.headers("set-cookie"));
            client.setCookie("Cookie", cookie);
        } catch (IOException e){
            e.printStackTrace();
            throw new EnterHcheckException("Failed to fetch server");
        }
        return this;
    }

    @SuppressWarnings("all")
    public HcheckRequest authorize() throws AuthorizeHcheckException {

        Map<String, String> params = new HashMap<>();
        params.put("qstnCrtfcNoEncpt", "");
        params.put("rtnRsltCode", "");
        params.put("schulCode", client.getSchoolCode());
        params.put("schulNm", client.getSchoolName());
        params.put("pName", client.getMyName());
        params.put("frnoRidno", client.getBirthday());
        params.put("aditCrtfcNo", "");

        OkHttpClient ohc = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.get(HcheckClient.contentType), Utils.bodyParser(params));
        Request request = new Request.Builder()
                .url(getAuthURL())
                .header("User-Agent", HcheckClient.userAgent)
                .header("X-Requested-With", HcheckClient.xReqWith)
                .header("Referer", client.getReferer())
                .header("Accept-Language", HcheckClient.acceptLang)
                .header("Cookie", client.getCookie("Cookie"))
                .post(requestBody)
                .build();
        String received_key = "";
        try {
            Response res = ohc.newCall(request).execute();
            JSONObject json = new JSONObject(res.body().string()).getJSONObject("resultSVO");
            received_key = json.getJSONObject("data").getString("rtnRsltCode");
            authorize_key = json.getJSONObject("data").getString("qstnCrtfcNoEncpt");

            if(!received_key.equals("SUCCESS")) throw new IllegalAccessException();
        } catch (IOException e){
            e.printStackTrace();
            throw new AuthorizeHcheckException("Failed to fetch server");
        } catch (IllegalAccessException e){
            e.printStackTrace();
            throw new AuthorizeHcheckException(String.format("Gets an error code: %s", received_key));
        } catch (JSONException e){
            e.printStackTrace();
            throw new AuthorizeHcheckException("Failed to extract info");
        }
        return this;
    }

    public HcheckRequest submit() throws SubmitHcheckException {

        Map<String, String> params = new HashMap<>();
        params.put("qstnCrtfcNoEncpt", Utils.encodeURI(authorize_key));
        params.put("rtnRsltCode", "SUCCESS");
        params.put("schulNm", "");
        params.put("stdntName", "");
        params.put("rspns01", "1");
        params.put("rspns02", "1");
        params.put("rspns07", "0");
        params.put("rspns08", "0");
        params.put("rspns09", "0");

        OkHttpClient ohc = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.get(HcheckClient.contentType), Utils.bodyParser(params));
        Request request = new Request.Builder()
                .url(getSubmitURL())
                .header("User-Agent", HcheckClient.userAgent)
                .header("X-Requested-With", HcheckClient.xReqWith)
                .header("Referer", client.getReferer())
                .header("Accept-Language", HcheckClient.acceptLang)
                .header("Cookie", client.getCookie("Cookie"))
                .post(requestBody)
                .build();
        String received_key = "";
        try {
            Response res = ohc.newCall(request).execute();
            JSONObject json = new JSONObject(res.body().string()).getJSONObject("resultSVO");
            received_key = json.getJSONObject("data").getString("rtnRsltCode");
            schoolName = json.getString("schulNm");
            studentName = json.getString("stdntName");

            if(!received_key.equals("SUCCESS")) throw new IllegalAccessException();
        } catch (IOException e){
            e.printStackTrace();
            throw new SubmitHcheckException("Failed to fetch server");
        } catch (IllegalAccessException e){
            e.printStackTrace();
            throw new SubmitHcheckException(String.format("Gets an error code: %s", received_key));
        } catch (JSONException e){
            e.printStackTrace();
            throw new SubmitHcheckException("Received empty data");
        }
        return this;
    }

    public void end() throws EndHcheckException {

        Map<String, String> params = new HashMap<>();
        params.put("qstnCrtfcNoEncpt", Utils.encodeURI(authorize_key));
        params.put("rtnRsltCode", "SUCCESS");
        params.put("schulNm", schoolName);
        params.put("stdntName", studentName);
        params.put("rspns01", "1");
        params.put("rspns02", "1");
        params.put("rspns07", "0");
        params.put("rspns08", "0");
        params.put("rspns09", "0");

        OkHttpClient ohc = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.get(HcheckClient.contentType), Utils.bodyParser(params));
        Request request = new Request.Builder()
                .url(getSubmitURL())
                .header("User-Agent", HcheckClient.userAgent)
                .header("X-Requested-With", HcheckClient.xReqWith)
                .header("Referer", client.getReferer())
                .header("Accept-Language", HcheckClient.acceptLang)
                .header("Cookie", client.getCookie("Cookie"))
                .post(requestBody)
                .build();
        String received_key = "";
        try {
            Response res = ohc.newCall(request).execute();
            JSONObject json = new JSONObject(res.body().string()).getJSONObject("resultSVO");
            received_key = json.getJSONObject("data").getString("rtnRsltCode");

            if(!received_key.equals("SUCCESS")) throw new IllegalAccessException();
        } catch (IOException e){
            e.printStackTrace();
            throw new EndHcheckException("Failed to fetch server");
        } catch (JSONException e){
            e.printStackTrace();
            throw new EndHcheckException("Received empty data");
        } catch (IllegalAccessException e){
            e.printStackTrace();
            throw new EndHcheckException(String.format("Gets an error code: %s", received_key));
        }
    }
}

