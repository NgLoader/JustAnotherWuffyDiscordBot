package eu.teamwuffy.wuffy.api.twitch.handler;

import com.squareup.okhttp.OkHttpClient;

import eu.teamwuffy.wuffy.api.twitch.TwitchAPI;

public class TwitchBitsHandler extends TwitchHandler {

	public TwitchBitsHandler(TwitchAPI twitchAPI, OkHttpClient httpClient, String clientId) {
		super(twitchAPI, httpClient, clientId);
	}
}