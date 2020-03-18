package org.karnak.api;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is for doing http request
 * This is a "static" class which just provide static methods to do a http request
 */
public class HTTPRequest {
    /**
     * Sending the request without body (for all requests but no POST)
     *
     * @param RESTServiceEndpoint Server adress
     * @param requestMethodType HTTP method of the request
     * @return Result of the request in HTTPRequestResult object
     */
    public static HTTPRequestResult submit(String RESTServiceEndpoint, HTTPRequestMethod requestMethodType) {
        return submit(RESTServiceEndpoint, requestMethodType, null);
    }

    /**
     * Send request
     *
     * @param RESTServiceEndpoint Server adress
     * @param requestMethodType HTTP method of the request
     * @param requestBodyParameters
     * @return Result of the request in HTTPRequestResult object
     */
    public static HTTPRequestResult submit(String RESTServiceEndpoint, HTTPRequestMethod requestMethodType, HashMap<String, String> requestBodyParameters) {
        HTTPRequestResult result = new HTTPRequestResult();
        String resultSTR = "";

        try {
            //build request
            URL url = new URL(RESTServiceEndpoint);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(requestMethodType.toString());
            urlConnection.setUseCaches(false);

            if (requestMethodType == HTTPRequestMethod.POST)
                urlConnection.setDoOutput(true); // This method is juste for POST request

            //Test if we need to create the request body
            if (requestBodyParameters != null) {
                String RequestBodyContentString = "";
                for(Map.Entry<String, String> entry : requestBodyParameters.entrySet())
                    RequestBodyContentString += entry.getKey() + "=" + entry.getValue() + "&";

                byte[] RequestBodyContent = RequestBodyContentString.getBytes();

                String charset = "UTF-8";
                String requestBodyContentType = "application/x-www-form-urlencoded;charset=" + charset;

                urlConnection.setRequestProperty("Accept-Charset", charset);
                urlConnection.setRequestProperty("Content-Type", requestBodyContentType);
                urlConnection.setRequestProperty("Content-Length", String.valueOf(RequestBodyContent.length));

                //Writing parameters in the request
                DataOutputStream requestStream = null;
                try {
                    requestStream = new DataOutputStream(urlConnection.getOutputStream());
                    requestStream.write(RequestBodyContent);

                } catch (Exception e) {
                    e.printStackTrace();
                    requestStream.flush();
                    requestStream.close();
                }
            }

            try {
                urlConnection.connect();

                //Take the answer
                result.responseCode = urlConnection.getResponseCode();

                //Reading the result of this request
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    resultSTR += line + "\n";
                }
                bufferedReader.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }
        } catch(Exception e) { }

        try {
            result.jsonObject = new JSONObject(resultSTR);
        } catch (JSONException e) {
            e.printStackTrace();
            result.jsonObject = null;
        }

        return result;
    }
}
