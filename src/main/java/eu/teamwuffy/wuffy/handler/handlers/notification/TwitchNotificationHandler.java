package eu.teamwuffy.wuffy.handler.handlers.notification;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import eu.teamwuffy.wuffy.api.twitch.TwitchAPI;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponse;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponseGame;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponseStream;
import eu.teamwuffy.wuffy.api.twitch.response.TwitchResponseUser;
import eu.teamwuffy.wuffy.handler.Handler;
import eu.teamwuffy.wuffy.shard.WuffyShard;
import eu.teamwuffy.wuffy.webhook.WebhookQueue;

public class TwitchNotificationHandler extends Handler implements Runnable {

	private static final String DEFAULT_EMBED_MESSAGE = "{\"username\":\"Twitch\",\"avatar_url\":\"https://wuffy.eu/pictures/example_avatar_300x300.png\",\"content\":\"@here **%n** ist jetzt live auf Twitch\",\"embeds\":[{\"title\":\"%t\",\"url\":\"%urll\",\"color\":6570405,\"timestamp\":\"%sa\",\"thumbnail\":{\"url\":\"%urlg300x300\"},\"image\":{\"url\":\"%urlt580x900\"},\"author\":{\"name\":\"%n\",\"url\":\"%urll\",\"icon_url\":\"%urlpi\"},\"footer\":{\"icon_url\":\"%urlpi\",\"text\":\"%n\"},\"fields\":[{\"name\":\"Game\",\"value\":\"%g\",\"inline\":true},{\"name\":\"Viewers\",\"value\":\"%vc\",\"inline\":true}]}]}";
	private static final SimpleDateFormat TWITCH_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss");

	private final TwitchAPI twitchAPI;

	private final Map<String, TwitchUser> users = new HashMap<>();
	private final Map<String, String> lastUpdate = new HashMap<>();

	private AtomicBoolean running = new AtomicBoolean(true);

	public TwitchNotificationHandler(WuffyShard wuffy) {
		super(wuffy);

		this.twitchAPI = new TwitchAPI("djqft5sttqq7erjbca758ugvy9vvvp");

		this.users.put("27967772", new TwitchUser("iamhara", "https://discordapp.com/api/webhooks/673671490318893071/Da4J3FZsgMBtRReQmt-TW2B5hoeDVXWD9xq_4vHmm-2yZdtFYgmyodoh9_LVyZh8hhOC", "{\"username\":\"Twitch\",\"avatar_url\":\"https://wuffy.eu/pictures/example_avatar_300x300.png\",\"content\":\"Hey @everyone! **Ich bin Live auf Twitch!**\",\"embeds\":[{\"title\":\"%t\",\"url\":\"%urll\",\"color\":6570405,\"timestamp\":\"%sa\",\"thumbnail\":{\"url\":\"%urlg300x300\"},\"image\":{\"url\":\"%urlt580x900\"},\"author\":{\"name\":\"%n\",\"url\":\"%urll\",\"icon_url\":\"%urlpi\"},\"footer\":{\"icon_url\":\"%urlpi\",\"text\":\"%n\"},\"fields\":[{\"name\":\"Game\",\"value\":\"%g\",\"inline\":true},{\"name\":\"Viewers\",\"value\":\"%vc\",\"inline\":true}]}]}"));
//		this.users.put("29565445", new TwitchUser("h0llylp", "https://discordapp.com/api/webhooks/673653838363885568/J75lgW-n7UoSMuQKjf8M1kCOntLbQ73cMeXEHdGRxOBo9I1Eu7gY3Aawwkd704shFnYZ", null));

//		new Thread(this).start();
	}

	@Override
	public void run() {
		while(this.running.get()) {
			try {
				Thread.sleep(10000);

				if (this.users.isEmpty()) {
					return;
				}

				Map<String, TwitchResponseUser> users = null;
				Map<String, TwitchResponseGame> games = null;
				TwitchResponse<TwitchResponseStream> streams = null;

				users = this.twitchAPI.getUserHandler().getByName(this.users.values().stream().map(user -> user.name).collect(Collectors.toList())).data.stream()
						.collect(Collectors.toMap(key -> key.id, value -> value));
				streams = this.twitchAPI.getStreamHandler().getById(users.keySet().stream()
						.collect(Collectors.toList()));
				games = this.twitchAPI.getGameHandler().getById(streams.data.stream()
						.filter(data -> data != null && data.game_id != null && data.type != null && data.type.equals("live"))
						.map(data -> data.game_id)
						.distinct()
						.collect(Collectors.toList())).data.stream()
							.collect(Collectors.toMap(key -> key.id, value -> value));

				for (TwitchResponseStream stream : streams.data) {
					if (stream == null || stream.type == null || !stream.type.equals("live")) {
						continue;
					}

					TwitchResponseUser userResponse = users.get(stream.user_id);
					TwitchUser user = this.users.get(stream.user_id);

					if (userResponse == null || user == null) {
						continue;
					}

					String lastUpdate = this.lastUpdate.getOrDefault(stream.user_id, "");
					if (lastUpdate.equals(stream.started_at)) {
						continue;
					}
					this.lastUpdate.put(stream.user_id, stream.started_at);

					System.out.println("[" + DATE_FORMAT.format(System.currentTimeMillis()) + "] "
							+ "TwitchAPI: " + user.name + " (" + stream.user_id + ") is now live. "
							+ "Started at '" + DATE_FORMAT.format(TWITCH_DATE_FORMAT.parse(stream.started_at)) + "'");

					WebhookQueue.add(user.webhook, (user.message == null || user.message.isEmpty() ? DEFAULT_EMBED_MESSAGE : user.message)
							.replace("%vc", Integer.toString(stream.viewer_count))
							.replace("%n", userResponse.display_name)
							.replace("%urll", String.format("https://www.twitch.tv/%s", userResponse.display_name))
							.replace("%t", stream.title)
							.replace("%urlt300x300", stream.thumbnail_url.replace("{width}", "300").replace("{height}", "300"))
							.replace("%urlt580x900", stream.thumbnail_url.replace("{width}", "320").replace("{height}", "180"))
							.replace("%urlpi", userResponse.profile_image_url)
							.replace("%urloi", userResponse.offline_image_url)
							.replace("%sa", stream.started_at)
							.replace("%g", games.get(stream.game_id).name)
							.replace("%urlg300x300", games.get(stream.game_id).box_art_url.replace("{width}", "300").replace("{height}", "300"))
							.replace("%urlg580x900", games.get(stream.game_id).box_art_url.replace("{width}", "320").replace("{height}", "180")));
				}
			} catch(Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				return;
			}
		}
	}

	private class TwitchUser {

		private final String name;
		private final String webhook;
		private final String message;

		public TwitchUser(String name, String webhook, String message) {
			this.name = name;
			this.webhook = webhook;
			this.message = message;
		}
	}
}