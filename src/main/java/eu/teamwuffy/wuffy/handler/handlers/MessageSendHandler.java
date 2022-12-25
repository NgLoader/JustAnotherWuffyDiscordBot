package eu.teamwuffy.wuffy.handler.handlers;

import eu.teamwuffy.wuffy.handler.Handler;
import eu.teamwuffy.wuffy.shard.WuffyShard;

public class MessageSendHandler extends Handler {

//	private final Snowflake guildId = Snowflake.of("456517698130542623");
//	private final Snowflake channelId = Snowflake.of("643526121094381589");
//	private final Snowflake messageId = Snowflake.of("660984501920333844");

	public MessageSendHandler(WuffyShard wuffy) {
		super(wuffy);

//		this.addEvent(GuildCreateEvent.class, this::onGuildCreateEvent);
	}

//	private void onGuildCreateEvent(GuildCreateEvent event) {
//		if (event.getGuild().getId().equals(Snowflake.of("399636339768819723"))) {
//			System.out.println("FOUND");
//			Guild guild = event.getGuild();
//
//			guild.getChannelById(Snowflake.of("472931925309390849")).subscribe(channel -> {
//				TextChannel ch = (TextChannel) channel;
//				ch.createWebhook(hook -> {
//					hook.setName("Wuffy Notification");
//					hook.setReason("Notifications");
//				}).subscribe();
//			});
//		}
//	}
//		if (event.getGuild().getId().equals(this.guildId)) {
//			event.getClient().getMessageById(this.channelId, this.messageId).subscribe(message -> {
//				message.edit(edit -> {
//					edit.setContent("");
//					edit.setEmbed(embed -> {
//						embed.setColor(Color.RED);
//						embed.setDescription("Hier k�nnt ihr euch zuk�nftig, für eine Rolle eintragen. Einfach die passende Reaktion anklicken.");
//						embed.addField("**Youtuber**", ":movie_camera:", true);
//						embed.addField("**Musiker**", ":musical_note:", true);
//						embed.addField("**Developer**", ":keyboard:", true);
//						embed.addField("**Grafiker**", ":art:", true);
//						embed.addField("**Streamer**", ":red_circle:", true);
//						embed.addField("** **", "** **", true);
//					});
//					edit.setContent("Hier k�nnt ihr euch zuk�nftig, für eine Rolle eintragen. Einfach die passende Reaktion anklicken\n\n:movie_camera:: Youtuber\n:musical_note::Musiker\n:keyboard::Developer\n:art::Grafiker\n:red_circle::Streamer");
//				}).subscribe();
//			});
//			event.getGuild().getChannelById(this.channelId).subscribe(channel -> {
//				((MessageChannel) channel).createMessage(message -> {
//					message.setContent("@everyone");
//					message.setEmbed(embed -> {
//						embed.setColor(Color.RED);
//						embed.setTitle("Update");
//						embed.setDescription("Ihr könnt euch nun unter **#rollen** eigene rollen zuweisen");
//					});
//				}).subscribe();
//			});
//		}
//	}
}