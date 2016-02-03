package io.kiah.common.pool.server.ping;

import io.kiah.common.config.listener.DaemonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 监控Server死活状态的后台任务调度类 Created by Kiah on 1/23/16.
 */
public class ApplicationStatusWatcher {

	private static final Logger log = LoggerFactory.getLogger(ApplicationStatusWatcher.class);

	private static final long scheduleDelay = 5000; // 5 Seconds
	private static final long schedulePeriod = 8000; // 8 Seconds

	private static final ScheduledExecutorService executor;
	private static final Map<String, ScheduledFuture<?>> futures;
	private static final Map<String, ServerHealthManager> managers;

	static {
		executor = Executors.newScheduledThreadPool(4, new DaemonThreadFactory());
		futures = new HashMap<String, ScheduledFuture<?>>(5);
		managers = new HashMap<String, ServerHealthManager>(5);
	}

	/**
	 * Cannot be instantiated.
	 */
	private ApplicationStatusWatcher() {
	}

	/**
	 * Register an application's connections to be monitored.
	 */
	public static synchronized void startWatch(final ServerHealthManager healthManager) {

		if (healthManager == null)
			return;

		String bizKey = healthManager.getBizKey();
		stopWatch(bizKey);

		ScheduledFuture<?> future = executor.scheduleAtFixedRate(new HealthCheckTask(healthManager), scheduleDelay,
				schedulePeriod, TimeUnit.MILLISECONDS);

		futures.put(bizKey, future);
		managers.put(bizKey, healthManager);

		log.info(String.format("Add \"%s\" to status watcher.", healthManager.toString()));
	}

	/**
	 * Remove an application from monitor list.
	 */
	public static synchronized void stopWatch(String bizKey) {

		if (futures.containsKey(bizKey)) {
			futures.get(bizKey).cancel(true); // Cancel task first.
			futures.remove(bizKey);
			managers.remove(bizKey);

			log.info("Remove " + bizKey + " from application status watcher.");
		}
	}

	/**
	 * Returns the server health manager object for specified application and
	 * serving biz.
	 */
	public static ServerHealthManager getHealthManager(String bizKey) {
		return managers.get(bizKey);
	}

	/**
	 * Represents a task for invoke check method of connection manager.
	 */
	private static class HealthCheckTask implements Runnable {

		private final ServerHealthManager healthManager;

		public HealthCheckTask(final ServerHealthManager healthManager) {
			this.healthManager = healthManager;
		}

		@Override
		public void run() {
			try {
				healthManager.check();
			} catch (Exception e) {
				// Catch all exceptions in order to prevent thread-exit.
				log.error("Error on checking search status of " + healthManager.toString(), e);
			}
		}
	}

}
