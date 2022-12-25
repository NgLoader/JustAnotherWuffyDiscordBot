package eu.teamwuffy.wuffy.api.twitch.handler;

import java.util.List;

import com.squareup.okhttp.OkHttpClient;

import eu.teamwuffy.wuffy.api.twitch.TwitchAPI;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponse;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponseStream;

public class TwitchStreamHandler extends TwitchHandler {

	public TwitchStreamHandler(TwitchAPI twitchAPI, OkHttpClient httpClient, String clientId) {
		super(twitchAPI, httpClient, clientId);
	}

	public TwitchResponse<TwitchResponseStream> getById(String userId) {
		return this.request(TwitchResponseStream.class, String.format("%sstreams?user_id=%s", TwitchAPI.TWITCH_REQUEST_URL, userId));
	}

	public TwitchResponse<TwitchResponseStream> getById(List<String> userIds) {
		return this.request(TwitchResponseStream.class, String.format("%sstreams?user_id=%s", TwitchAPI.TWITCH_REQUEST_URL, String.join("&user_id=", userIds)));
	}
}