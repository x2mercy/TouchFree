package com.jblearning.kitchen;
import com.loopj.android.http.*;

public class RecipeRequest {
    private static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/";
    private static final String API_KEY = "ua9TN5jI9Zmsh2GQruoJx9GDuB6kp16z22FjsnpoTwy1GJRizA";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("X-Mashape-Key",API_KEY);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
