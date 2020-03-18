package org.karnak.api;

import org.json.JSONObject;

/**
 * Object result of a http request
 */
public class HTTPRequestResult {
    public int responseCode = -1;
    public JSONObject jsonObject = new JSONObject();
}
