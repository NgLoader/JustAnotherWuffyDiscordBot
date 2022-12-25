package eu.teamwuffy.wuffy.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import eu.teamwuffy.wuffy.shard.WuffyShard;

public class HandlerRegistry {

	private static final Map<Class<? extends Handler>, HandlerList> CLASS_TO_HANDLER_LIST = new IdentityHashMap<>();
	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);

	private static final List<Class<? extends Handler>> HANDLERS = Collections.synchronizedList(new ArrayList<>());
	private static final Map<WuffyShard, List<Handler>> SHARDS = new ConcurrentHashMap<>();

	private static HandlerList getHandlerList(Class<? extends Handler> handlerClass) {
		HandlerRegistry.LOCK.readLock().lock();
		HandlerList handlerList = HandlerRegistry.CLASS_TO_HANDLER_LIST.get(handlerClass);
		HandlerRegistry.LOCK.readLock().unlock();

		if (handlerList == null) {
			handlerList = new HandlerList(handlerClass);

			HandlerRegistry.LOCK.writeLock().lock();
			HandlerRegistry.CLASS_TO_HANDLER_LIST.putIfAbsent(handlerClass, handlerList);
			handlerList = HandlerRegistry.CLASS_TO_HANDLER_LIST.get(handlerClass);
			HandlerRegistry.LOCK.writeLock().unlock();
		}

		return handlerList;
	}

	public static void registerShard(WuffyShard shard) {
		List<Handler> handlers = HandlerRegistry.SHARDS.computeIfAbsent(shard, K -> Collections.synchronizedList(new ArrayList<>()));

		for (Class<? extends Handler> handlerClass : HandlerRegistry.HANDLERS) {
			Handler handler = HandlerRegistry.getHandlerList(handlerClass).register(shard);

			if (handler != null) {
				handlers.add(handler);
			}
		}
	}

	public static void unregisterShard(WuffyShard shard) {
		List<Handler> handlers = HandlerRegistry.SHARDS.remove(shard);

		if (handlers == null) {
			return;
		}

		for (Handler handler : handlers) {
			HandlerRegistry.getHandlerList(handler.getClass()).unregister(handler);
		}
	}

	public static void registerHandler(Class<? extends Handler> handler) {
		HandlerRegistry.HANDLERS.add(handler);

		HandlerList handlerList = HandlerRegistry.getHandlerList(handler);
		for (WuffyShard shard : HandlerRegistry.SHARDS.keySet()) {
			handlerList.register(shard);
		}
	}

	public static void unregisterHandler(Class<? extends Handler> handler) {
		HandlerList handlerList = HandlerRegistry.getHandlerList(handler);

		HandlerRegistry.HANDLERS.remove(handler);

		HandlerRegistry.LOCK.writeLock().lock();
		HandlerRegistry.CLASS_TO_HANDLER_LIST.remove(handler);
		HandlerRegistry.LOCK.writeLock().unlock();

		handlerList.unregisterAll();
	}
}