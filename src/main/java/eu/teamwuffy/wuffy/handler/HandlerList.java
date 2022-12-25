package eu.teamwuffy.wuffy.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import eu.teamwuffy.wuffy.shard.WuffyShard;

public class HandlerList {

	private final List<Handler> handlers = new ArrayList<>();
	private final Semaphore semaphore = new Semaphore(1, true);

	private final Constructor<? extends Handler> constructor;

	public HandlerList(Class<? extends Handler> handlerClass) {
		try {
			this.constructor = handlerClass.getConstructor(WuffyShard.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("Unable to find default handler constructor", e);
		}
	}

	public Handler register(WuffyShard shard) {
		try {
			Handler handler = this.constructor.newInstance(shard);
			handler.register(shard.getClient().getEventDispatcher());

			this.semaphore.acquireUninterruptibly();
			this.handlers.add(handler);
			this.semaphore.release();

			return handler;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void unregister(Handler handler) {
		this.semaphore.acquireUninterruptibly();
		this.handlers.remove(handler);
		this.semaphore.release();

		handler.unregister();
	}

	public void unregisterAll() {
		this.semaphore.acquireUninterruptibly();
		for (Handler handler : this.handlers) {
			handler.unregister();
		}
		this.handlers.clear();
		this.semaphore.release();
	}
}