package eu.teamwuffy.wuffy.api;

import java.util.ArrayList;
import java.util.List;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;

public class TeamSpeak3API {

	private final TS3Config config;
	private final TS3Query query;

	private TS3Api api;

	private List<TS3Listener> listeners = new ArrayList<>();

	public TeamSpeak3API() {
		this.config = new TS3Config();
		config.setHost("ts.ludum-network.de");

		this.query = new TS3Query(config);
	}

	public void login()	 {
		try {
			if (this.api != null) {
				return;
			}

			this.query.connect();

			this.api = query.getApi();
			this.api.login("", "");
			this.api.selectVirtualServerById(1);
			this.api.setNickname("Wuffy");

			this.api.registerAllEvents();
			this.listeners.forEach(listener -> this.api.removeTS3Listeners(listener));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error by login with teamspeak3!");
		}
	}

	public void logout() {
		if (this.api == null) {
			return;
		}

		this.api = null;
		this.query.exit();
	}

	public void addListener(TS3Listener listener) {
		if (this.listeners.contains(listener)) {
			return;
		}

		this.listeners.add(listener);

		if (this.api != null) {
			this.api.addTS3Listeners(listener);
		}
	}

	public void removeListener(TS3Listener listener) {
		if (!this.listeners.contains(listener)) {
			return;
		}

		this.listeners.remove(listener);

		if (this.api != null) {
			this.api.removeTS3Listeners(listener);
		}
	}

	public TS3Api getApi() {
		return this.api;
	}
}