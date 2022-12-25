package eu.teamwuffy.wuffy.shard;

import discord4j.core.DiscordClient;
import eu.teamwuffy.wuffy.Wuffy;
import eu.teamwuffy.wuffy.handler.HandlerRegistry;

public class WuffyShard {

	private final Wuffy shard;
	private final DiscordClient client;

	public WuffyShard(Wuffy wuffy, DiscordClient client) {
		this.shard = wuffy;
		this.client = client;

		HandlerRegistry.registerShard(this);

		this.client.login().subscribe();
	}

	public Wuffy getWuffy() {
		return this.shard;
	}

	public DiscordClient getClient() {
		return this.client;
	}
}