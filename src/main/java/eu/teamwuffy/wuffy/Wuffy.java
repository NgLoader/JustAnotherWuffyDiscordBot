package eu.teamwuffy.wuffy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.shard.ShardingClientBuilder;
import eu.teamwuffy.wuffy.api.TeamSpeak3API;
import eu.teamwuffy.wuffy.handler.HandlerRegistry;
import eu.teamwuffy.wuffy.handler.handlers.ReactionGroupHandler;
import eu.teamwuffy.wuffy.shard.WuffyShard;

public class Wuffy {

	public static void main(String[] args) {
		new Wuffy(args);
	}

	private final Path configPath = Paths.get("./wuffy/config.properties");
	private final Properties config = new Properties();

	private final TeamSpeak3API teamSpeak3API = new TeamSpeak3API();

	public Wuffy(String[] args) {
		// Load config
		if (Files.exists(this.configPath)) {
			try (InputStream inputStream = Files.newInputStream(configPath)) {
				this.config.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error by reading config!");
			}
		}

		// Add config save on shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (Files.notExists(configPath.getParent())) {
					try {
						Files.createDirectories(configPath.getParent());
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Error by creating directory!");
					}
				}

				try (OutputStream outputStream = Files.newOutputStream(configPath, StandardOpenOption.CREATE)) {
					config.store(outputStream, Long.toString(System.currentTimeMillis()));
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Error by saveing config!");
				}
			}
		});

		// Try to get token
		String token = config.getProperty("token");
		if (token == null) {
			config.setProperty("token", "");
			System.out.println("Please enter your token!");
			System.exit(1);
			return;
		}

		HandlerRegistry.registerHandler(ReactionGroupHandler.class);
//		HandlerRegistry.registerHandler(MessageSendHandler.class);
//		HandlerRegistry.registerHandler(TwitchNotificationHandler.class);
//		HandlerRegistry.registerHandler(TeamspeakOnlineCounterHandler.class);

		// Load discord
		new ShardingClientBuilder(token)
		.build()
		.subscribe(this::createShard);

//		this.teamSpeak3API.login();

		System.out.println("Wuffy is now starting!");

		while(true);
	}

	private void createShard(DiscordClientBuilder builder) {
		new WuffyShard(this, builder.build());
	}

	public Properties getConfig() {
		return this.config;
	}

	public Path getConfigPath() {
		return this.configPath;
	}

	public TeamSpeak3API getTeamSpeak3API() {
		return this.teamSpeak3API;
	}
}