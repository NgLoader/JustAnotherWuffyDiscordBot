package eu.teamwuffy.wuffy.handler.handlers;

import java.awt.Color;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import eu.teamwuffy.wuffy.api.TeamSpeak3API;
import eu.teamwuffy.wuffy.handler.Handler;
import eu.teamwuffy.wuffy.shard.WuffyShard;

public class TeamspeakOnlineCounterHandler extends Handler {

	private final TeamSpeak3API ts3API;
	private long lastCount = 0;
	private long highestCount = 0;

	private TextChannel textChannel;
	private Message lastMessage;

	public TeamspeakOnlineCounterHandler(WuffyShard wuffy) {
		super(wuffy);

		this.addEvent(GuildCreateEvent.class, this::onGuildCreateEvent);

		this.ts3API = wuffy.getWuffy().getTeamSpeak3API();
		this.registerListener();
	}

	private void onGuildCreateEvent(GuildCreateEvent event) {
		if (this.textChannel != null) {
			return;
		}

		Guild guild = event.getGuild();
		DiscordClient client = event.getClient();

		if (guild.getId().asString().equals("330812207598272515")) {
			client.getChannelById(Snowflake.of("343187634388205568")).subscribe(channel -> {
				if (channel == null) {
					return;
				}

				this.textChannel = (TextChannel) channel;

				((TextChannel) channel).getMessageById(Snowflake.of("")).subscribe(message -> {
					if (message == null) {
						return;
					}

					this.lastMessage = message;
				});
			});
		}
	}

	private void checkOnlineCount() {
		TS3Api ts3Api = this.ts3API.getApi();

		if (ts3Api == null) {
			return;
		}

		long count = ts3Api.getClients().stream().filter(client -> !client.isServerQueryClient()).count();

		if (this.lastCount != count) {
			this.lastCount = count;

			if (this.highestCount < count) {
				this.highestCount = count;
			}

			if (lastMessage == null) {
				if (this.textChannel == null) {
					return;
				}

				this.lastMessage = this.textChannel.createMessage("").block();
			}

			this.lastMessage.edit(edit -> {
				edit.setEmbed(embed -> {
					embed.setDescription("Hier könnte ihre werbung stehen :kappa:");
					embed.addField("Derzeitig online", "" + this.lastCount, true);
					embed.addField("Höchster Rekord", "" + this.highestCount, true);
					embed.setColor(Color.CYAN);
				});
			}).subscribe();
		}
	}

	private void registerListener() {
		this.wuffy.getWuffy().getTeamSpeak3API().addListener(new TS3Listener() {
			
			@Override
			public void onTextMessage(TextMessageEvent event) {
				checkOnlineCount();
				System.out.println("MESSAGE");
			}
			
			@Override
			public void onServerEdit(ServerEditedEvent event) { }
			
			@Override
			public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent event) { }
			
			@Override
			public void onClientMoved(ClientMovedEvent event) { }
			
			@Override
			public void onClientLeave(ClientLeaveEvent event) {
				checkOnlineCount();
			}
			
			@Override
			public void onClientJoin(ClientJoinEvent event) {
				checkOnlineCount();
			}
			
			@Override
			public void onChannelPasswordChanged(ChannelPasswordChangedEvent event) { }
			
			@Override
			public void onChannelMoved(ChannelMovedEvent event) { }
			
			@Override
			public void onChannelEdit(ChannelEditedEvent event) { }
			
			@Override
			public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent event) { }
			
			@Override
			public void onChannelDeleted(ChannelDeletedEvent event) { }
			
			@Override
			public void onChannelCreate(ChannelCreateEvent event) { }
		});
	}
}