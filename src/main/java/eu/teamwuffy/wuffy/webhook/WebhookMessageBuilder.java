package eu.teamwuffy.wuffy.webhook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import discord4j.rest.json.request.EmbedRequest;
import discord4j.rest.json.request.EmbedRequest.Builder;
import eu.teamwuffy.wuffy.util.GsonUtil;

public class WebhookMessageBuilder {

	public static WebhookMessageBuilder fromJson(String json) {
		if(json != null)
			try {
				return GsonUtil.GSON.fromJson(json, WebhookMessageBuilder.class);
			} catch(Exception e) {
				e.printStackTrace();
			}

		return null;
	}

	private String content;
	private String username;
	private String avatar_url;
	private boolean tts;
	private List<EmbedRequest> embeds = new ArrayList<EmbedRequest>();

	public WebhookMessageBuilder setContent(String content) {
		this.content = content;

		return this;
	}

	public WebhookMessageBuilder setUsername(String username) {
		this.username = username;

		return this;
	}

	public WebhookMessageBuilder setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;

		return this;
	}

	public WebhookMessageBuilder setTts(boolean tts) {
		this.tts = tts;

		return this;
	}

	public WebhookMessageBuilder setEmbeds(List<EmbedRequest> embeds) {
		this.embeds = embeds;

		return this;
	}

	public WebhookMessageBuilder addEmbed(EmbedRequest embed) {
		this.embeds.add(embed);

		return this;
	}

	public WebhookMessageBuilder addEmbed(Builder builder) {
		this.embeds.add(builder.build());
		return this;
	}

	public String build() {
		JSONObject object = new JSONObject();

		if(this.content != null)
			object.put("content", this.content);
		if(this.username != null)
			object.put("username", this.username);
		if(this.avatar_url != null)
			object.put("avatar_url", this.avatar_url);
		if(this.tts)
			object.put("tts", this.tts);
		if(!this.embeds.isEmpty())
			object.put("embeds", new JSONArray(this.embeds.stream().map(embed -> new JSONObject(embed.toString())).collect(Collectors.toList())));

		return object.toString();
	}
}