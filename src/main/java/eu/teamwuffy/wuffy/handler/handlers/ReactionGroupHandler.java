package eu.teamwuffy.wuffy.handler.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.reaction.ReactionEmoji.Unicode;
import discord4j.core.object.util.Snowflake;
import eu.teamwuffy.wuffy.handler.Handler;
import eu.teamwuffy.wuffy.shard.WuffyShard;

public class ReactionGroupHandler extends Handler {

	//			Guild			Channel		Message			Reaction	Role
	private Map<Snowflake, Map<Snowflake, Map<Snowflake, Map<ReactionEmoji, Snowflake>>>> data = new HashMap<>();

	public ReactionGroupHandler(WuffyShard shard) {
		super(shard);

		this.addEvent(GuildCreateEvent.class, this::onGuildCreateEvent);
		this.addEvent(ReactionAddEvent.class, this::onReactionAddEvent);
		this.addEvent(ReactionRemoveEvent.class, this::onReactionRemoveEvent);

		this.addReactionMessage("456517698130542623", "643526121094381589", "660984501920333844", ReactionEmoji.unicode("🔴"), "497464281135054864"); // Streamer
		this.addReactionMessage("456517698130542623", "643526121094381589", "660984501920333844", ReactionEmoji.unicode("🎥"), "497464434126487572"); // Youtuber
		this.addReactionMessage("456517698130542623", "643526121094381589", "660984501920333844", ReactionEmoji.unicode("🎵"), "497464845650624515"); // Musiker
		this.addReactionMessage("456517698130542623", "643526121094381589", "660984501920333844", ReactionEmoji.unicode("🎨"), "497464973816233994"); // Grafiker
		this.addReactionMessage("456517698130542623", "643526121094381589", "660984501920333844", ReactionEmoji.unicode("⌨️"), "497465377203159040"); // Developer
	}

	private void onGuildCreateEvent(GuildCreateEvent event) {
		Guild guild = event.getGuild();
		DiscordClient client = event.getClient();
		if (this.data.containsKey(guild.getId())) {
			Map<Snowflake, Map<Snowflake, Map<ReactionEmoji, Snowflake>>> channels = this.data.get(guild.getId());

			for (Entry<Snowflake, Map<Snowflake, Map<ReactionEmoji, Snowflake>>> entry : channels.entrySet()) {
				for (Entry<Snowflake, Map<ReactionEmoji, Snowflake>> entryMessage : entry.getValue().entrySet()) {
					client.getMessageById(entry.getKey(), entryMessage.getKey()).subscribe(message -> {
						for (ReactionEmoji reaction : entryMessage.getValue().keySet()) {
							message.addReaction(reaction).subscribe();
						}
					}, error -> {
						error.printStackTrace();
						System.out.println("MessageReaction was removed from (Guild: " + guild.getId().asString() + ", Message: " + entryMessage.getKey() + ")");
						this.data.get(guild.getId()).get(entry.getKey()).remove(entryMessage.getKey());
					});
				}
			}
		}
	}

	private void onReactionAddEvent(ReactionAddEvent event) {
		event.getUser().subscribe(user -> {
			if (!user.isBot()) {
				if (event.getGuildId().isPresent() && this.data.containsKey(event.getGuildId().get())) {
					Map<Snowflake, Map<Snowflake, Map<ReactionEmoji, Snowflake>>> channels = this.data.get(event.getGuildId().get());

					if (channels.containsKey(event.getChannelId())) {
						Map<Snowflake, Map<ReactionEmoji, Snowflake>> messages = channels.get(event.getChannelId());

						if (messages.containsKey(event.getMessageId())) {
							Map<ReactionEmoji, Snowflake> reactions = messages.get(event.getMessageId());

							if (reactions.containsKey(event.getEmoji())) {
								user.asMember(event.getGuildId().get())
									.subscribe(member -> {
										member.addRole(reactions.get(event.getEmoji()), "Automaticly by wuffy").subscribe();

										Optional<Unicode> emoji = event.getEmoji().asUnicodeEmoji();
										if (emoji.isPresent()) {
											System.out.println("ReactionGroup: user: " + event.getUserId().asString() + " (" + member.getDisplayName() + ")" + " join role: " + reactions.get(event.getEmoji()).asString() + " (" + emoji.get().getRaw() + ")");
										} else {
											System.out.println("ReactionGroup: user: " + event.getUserId().asString() + " (" + member.getDisplayName() + ")" + " join role: " + reactions.get(event.getEmoji()).asString());
										}
									});
							}
						}
					}
				}
			}
		});
	}

	private void onReactionRemoveEvent(ReactionRemoveEvent event) {
		event.getUser().subscribe(user -> {
			if (!user.isBot()) {
				if (event.getGuildId().isPresent() && this.data.containsKey(event.getGuildId().get())) {
					Map<Snowflake, Map<Snowflake, Map<ReactionEmoji, Snowflake>>> channels = this.data.get(event.getGuildId().get());

					if (channels.containsKey(event.getChannelId())) {
						Map<Snowflake, Map<ReactionEmoji, Snowflake>> messages = channels.get(event.getChannelId());

						if (messages.containsKey(event.getMessageId())) {
							Map<ReactionEmoji, Snowflake> reactions = messages.get(event.getMessageId());

							if (reactions.containsKey(event.getEmoji())) {
								user.asMember(event.getGuildId().get())
									.subscribe(member -> {
										member.removeRole(reactions.get(event.getEmoji()), "Automaticly by wuffy").subscribe();

										Optional<Unicode> emoji = event.getEmoji().asUnicodeEmoji();
										if (emoji.isPresent()) {
											System.out.println("ReactionGroup: user: " + event.getUserId().asString() + " (" + member.getDisplayName() + ")" + " left role: " + reactions.get(event.getEmoji()).asString() + " (" + emoji.get().getRaw() + ")");
										} else {
											System.out.println("ReactionGroup: user: " + event.getUserId().asString() + " (" + member.getDisplayName() + ")" + " left role: " + reactions.get(event.getEmoji()).asString());
										}
									});
							}
						}
					}
				}
			}
		});
	}

	private void addReactionMessage(String guildId, String channelId, String messageId, ReactionEmoji emoji, String roleId) {
		this.addReactionMessage(Snowflake.of(guildId), Snowflake.of(channelId), Snowflake.of(messageId), emoji, Snowflake.of(roleId));
	}

	private void addReactionMessage(Snowflake guildId, Snowflake channelId, Snowflake messageId, ReactionEmoji emoji, Snowflake roleId) {
		Map<Snowflake, Map<Snowflake, Map<ReactionEmoji, Snowflake>>> channels = this.data.get(guildId);
		if (channels == null) {
			channels = new HashMap<>();
			this.data.put(guildId, channels);
		}


		Map<Snowflake, Map<ReactionEmoji, Snowflake>> messages = channels.get(channelId);
		if (messages == null) {
			messages = new HashMap<>();
			channels.put(channelId, messages);
		}

		Map<ReactionEmoji, Snowflake> reactions = messages.get(messageId);
		if (reactions == null) {
			reactions = new HashMap<>();
			messages.put(messageId, reactions);
		}

		reactions.put(emoji, roleId);
	}
}