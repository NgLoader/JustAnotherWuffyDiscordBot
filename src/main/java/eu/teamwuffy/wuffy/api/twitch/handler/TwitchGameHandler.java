package eu.teamwuffy.wuffy.api.twitch.handler;

import java.util.List;

import com.squareup.okhttp.OkHttpClient;

import eu.teamwuffy.wuffy.api.twitch.TwitchAPI;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponse;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponseGame;

public class TwitchGameHandler extends TwitchHandler {

	public TwitchGameHandler(TwitchAPI twitchAPI, OkHttpClient httpClient, String clientId) {
		super(twitchAPI, httpClient, clientId);
	}

	public TwitchResponse<TwitchResponseGame> getById(String id) {
		return this.request(TwitchResponseGame.class, String.format("%sgames?id=%s", TwitchAPI.TWITCH_REQUEST_URL, id));
	}

	public TwitchResponse<TwitchResponseGame> getById(List<String> ids) {
		return this.request(TwitchResponseGame.class, String.format("%sgames?id=%s", TwitchAPI.TWITCH_REQUEST_URL, String.join("&id=", ids)));
	}

	public TwitchResponse<TwitchResponseGame> getByName(String name) {
		return this.request(TwitchResponseGame.class, String.format("%sgames?name=%s", TwitchAPI.TWITCH_REQUEST_URL, name));
	}

	public TwitchResponse<TwitchResponseGame> getByName(List<String> names) {
		return this.request(TwitchResponseGame.class, String.format("%sgames?name=%s", TwitchAPI.TWITCH_REQUEST_URL, String.join("&name=", names)));
	}
}