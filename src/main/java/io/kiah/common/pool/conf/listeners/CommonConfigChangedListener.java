package io.kiah.common.pool.conf.listeners;

import io.kiah.common.config.listener.IConfigChangedListener;
import io.kiah.common.config.register.Config;
import io.kiah.common.pool.conf.ConfigurationManager;

/**
 * Created by Kiah on 1/24/16.
 */
public class CommonConfigChangedListener implements IConfigChangedListener {

	@Override
	public void configChanged(Config config) {
		ConfigurationManager.reload(config.getXmlPath());
	}
}
