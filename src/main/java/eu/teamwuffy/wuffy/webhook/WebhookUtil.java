package eu.teamwuffy.wuffy.webhook;

import java.io.IOException;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class WebhookUtil {

	private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	public static String send(String webhookURL, WebhookMessageBuilder builder) {
		return WebhookUtil.send(webhookURL, builder.build());
	}

	public static String send(String webhookURL, String message) {
		Request request = new Request.Builder()
				.header("Content-Type", "application/json")
				.url(webhookURL)
				.post(RequestBody.create(WebhookUtil.MEDIA_TYPE_JSON, message))
				.build();

		try {
			return WebRequestBuilder.request(request).body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}