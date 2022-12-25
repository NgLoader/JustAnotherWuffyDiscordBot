package eu.teamwuffy.wuffy.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import eu.teamwuffy.wuffy.shard.WuffyShard;
import reactor.core.Disposable;

public abstract class Handler {

	protected final WuffyShard wuffy;

	protected List<EventListener<? extends Event>> events = new ArrayList<>();

	public Handler(WuffyShard wuffy) {
		this.wuffy = wuffy;
	}

	public void register(EventDispatcher dispatcher) {
		this.events.forEach(event -> event.register(dispatcher));
	}

	public void unregister() {
		this.events.forEach(event -> event.unregister());
	}

	public <T extends Event> void addEvent(Class<T> type, Consumer<T> consumer) {
		this.events.add(new EventListener<T>(type, consumer));
	}

	private class EventListener<T extends Event> {

		private final Class<T> type;
		private final Consumer<T> consumer;

		private Disposable disposable;

		public EventListener(Class<T> type, Consumer<T> consumer) {
			this.type = type;
			this.consumer = consumer;
		}

		public void register(EventDispatcher dispatcher) {
			this.disposable = dispatcher.on(this.type).subscribe(this.consumer);
		}

		public void unregister() {
			if (this.disposable != null) {
				this.disposable.dispose();
			}
		}
	}
}
