package eu.teamwuffy.wuffy.webhook;

import java.io.IOException;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class WebRequestBuilder {

	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

	public static void request(Request request, Callback responseCallback) {
		WebRequestBuilder.HTTP_CLIENT.newCall(request).enqueue(responseCallback);
	}

	public static Response request(Request request) throws IOException {
		return WebRequestBuilder.HTTP_CLIENT.newCall(request).execute();
	}
}