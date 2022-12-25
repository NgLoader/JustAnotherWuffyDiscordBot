package eu.teamwuffy.wuffy.api.twitch.handler;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import eu.teamwuffy.wuffy.api.twitch.TwitchAPI;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponse;
import eu.teamwuffy.wuffy.util.GsonUtil;

public class TwitchHandler {

	protected final TwitchAPI twitchAPI;

	protected final OkHttpClient httpClient;

	private String clientId;

	public TwitchHandler(TwitchAPI twitchAPI, OkHttpClient httpClient, String clientId) {
		this.twitchAPI = twitchAPI;
		this.httpClient = httpClient;
		this.clientId = clientId;
	}

	protected <T extends TwitchResponse<?>> T request(Class<T> requestClass, String url) {
		try {
			Response response = this.httpClient.newCall(new Request.Builder()
					.header("Client-ID", this.clientId)
					.url(url)
					.build()).execute();

			return GsonUtil.GSON.fromJson(response.body().string(), requestClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public TwitchAPI getAPI() {
		return this.twitchAPI;
	}
}