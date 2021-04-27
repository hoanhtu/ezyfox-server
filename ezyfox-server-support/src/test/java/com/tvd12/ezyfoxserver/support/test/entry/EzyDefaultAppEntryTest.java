package com.tvd12.ezyfoxserver.support.test.entry;

import static org.mockito.Mockito.spy;

import java.util.concurrent.ScheduledExecutorService;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.bean.EzyBeanContextBuilder;
import com.tvd12.ezyfox.concurrent.EzyErrorScheduledExecutorService;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.EzySimpleApplication;
import com.tvd12.ezyfoxserver.EzySimpleServer;
import com.tvd12.ezyfoxserver.EzySimpleZone;
import com.tvd12.ezyfoxserver.app.EzyAppRequestController;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.context.EzySimpleAppContext;
import com.tvd12.ezyfoxserver.context.EzySimpleServerContext;
import com.tvd12.ezyfoxserver.context.EzySimpleZoneContext;
import com.tvd12.ezyfoxserver.entity.EzyAbstractSession;
import com.tvd12.ezyfoxserver.entity.EzySimpleUser;
import com.tvd12.ezyfoxserver.event.EzySimpleUserRequestAppEvent;
import com.tvd12.ezyfoxserver.event.EzyUserRequestAppEvent;
import com.tvd12.ezyfoxserver.setting.EzyEventControllersSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleAppSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleEventControllersSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleSettings;
import com.tvd12.ezyfoxserver.setting.EzySimpleZoneSetting;
import com.tvd12.ezyfoxserver.support.entry.EzyDefaultAppEntry;
import com.tvd12.ezyfoxserver.support.entry.EzySimpleAppEntry;
import com.tvd12.ezyfoxserver.wrapper.EzyAppUserManager;
import com.tvd12.ezyfoxserver.wrapper.EzyEventControllers;
import com.tvd12.ezyfoxserver.wrapper.impl.EzyAppUserManagerImpl;
import com.tvd12.ezyfoxserver.wrapper.impl.EzyEventControllersImpl;

public class EzyDefaultAppEntryTest {

	@Test
	public void test() throws Exception {
		EzySimpleSettings settings = new EzySimpleSettings();
		EzySimpleServer server = new EzySimpleServer();
		server.setSettings(settings);
		EzySimpleServerContext serverContext = new EzySimpleServerContext();
		serverContext.setServer(server);
		serverContext.init();
		
		EzySimpleZoneSetting zoneSetting = new EzySimpleZoneSetting();
		EzySimpleZone zone = new EzySimpleZone();
		zone.setSetting(zoneSetting);
		EzySimpleZoneContext zoneContext = new EzySimpleZoneContext();
		zoneContext.setZone(zone);
		zoneContext.init();
		zoneContext.setParent(serverContext);
		
		EzySimpleAppSetting appSetting = new EzySimpleAppSetting();
		appSetting.setName("test");
		
		EzyAppUserManager appUserManager = EzyAppUserManagerImpl.builder()
				.build();

		EzyEventControllersSetting eventControllersSetting = new EzySimpleEventControllersSetting();
		EzyEventControllers eventControllers = EzyEventControllersImpl.create(eventControllersSetting);
		EzySimpleApplication application = new EzySimpleApplication();
		application.setSetting(appSetting);
		application.setUserManager(appUserManager);
		application.setEventControllers(eventControllers);
		
		ScheduledExecutorService appScheduledExecutorService = new EzyErrorScheduledExecutorService("not implement");
		EzySimpleAppContext appContext = new EzySimpleAppContext();
		appContext.setApp(application);
		appContext.setParent(zoneContext);
		appContext.setExecutorService(appScheduledExecutorService);
		appContext.init();
		
		EzySimpleAppEntry entry = new EzyAppEntryEx();
		entry.config(appContext);
		entry.start();
		handleClientRequest(appContext);
		entry.destroy();
	}
	
	private void handleClientRequest(EzyAppContext context) {
		EzySimpleApplication app = (EzySimpleApplication) context.getApp();
		EzyAppRequestController requestController = app.getRequestController();
		
		EzyAbstractSession session = spy(EzyAbstractSession.class);
		EzySimpleUser user = new EzySimpleUser();
		EzyArray data = EzyEntityFactory.newArrayBuilder()
				.append("chat")
				.append(EzyEntityFactory.newObjectBuilder()
						.append("message", "greet"))
				.build();
		EzyUserRequestAppEvent event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("chat")
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("no command")
				.append(EzyEntityFactory.newObjectBuilder()
						.append("message", "greet"))
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("noUser")
				.append(EzyEntityFactory.newObjectBuilder()
						.append("message", "greet"))
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("noSession")
				.append(EzyEntityFactory.newObjectBuilder()
						.append("message", "greet"))
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("noDataBinding")
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("badRequestSend")
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("badRequestNoSend")
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("exception")
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		try {
			requestController.handle(context, event);
		}
		catch (Exception e) {
			assert e instanceof IllegalStateException;
		}
		
		data = EzyEntityFactory.newArrayBuilder()
				.append("app")
				.build();
		event = new EzySimpleUserRequestAppEvent(user, session, data);
		requestController.handle(context, event);
	}
	
	@Test
	public void test2() throws Exception {
		EzySimpleSettings settings = new EzySimpleSettings();
		EzySimpleServer server = new EzySimpleServer();
		server.setSettings(settings);
		EzySimpleServerContext serverContext = new EzySimpleServerContext();
		serverContext.setServer(server);
		serverContext.init();
		
		EzySimpleZoneSetting zoneSetting = new EzySimpleZoneSetting();
		EzySimpleZone zone = new EzySimpleZone();
		zone.setSetting(zoneSetting);
		EzySimpleZoneContext zoneContext = new EzySimpleZoneContext();
		zoneContext.setZone(zone);
		zoneContext.init();
		zoneContext.setParent(serverContext);
		
		EzySimpleAppSetting appSetting = new EzySimpleAppSetting();
		appSetting.setName("test");
		
		EzyAppUserManager appUserManager = EzyAppUserManagerImpl.builder()
				.build();

		EzyEventControllersSetting eventControllersSetting = new EzySimpleEventControllersSetting();
		EzyEventControllers eventControllers = EzyEventControllersImpl.create(eventControllersSetting);
		EzySimpleApplication application = new EzySimpleApplication();
		application.setSetting(appSetting);
		application.setUserManager(appUserManager);
		application.setEventControllers(eventControllers);
		
		ScheduledExecutorService appScheduledExecutorService = new EzyErrorScheduledExecutorService("not implement");
		EzySimpleAppContext appContext = new EzySimpleAppContext();
		appContext.setApp(application);
		appContext.setParent(zoneContext);
		appContext.setExecutorService(appScheduledExecutorService);
		appContext.init();
		
		EzySimpleAppEntry entry = new EzyAppEntryEx2();
		entry.config(appContext);
		entry.start();
		entry.destroy();
	}
	
	public static class EzyAppEntryEx extends EzyDefaultAppEntry {

		@Override
		protected String[] getScanableBeanPackages() {
			return new String[] {
					"com.tvd12.ezyfoxserver.support.test.entry"
			};
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		protected Class[] getPrototypeClasses() {
			return new Class[] {
					ClientAppRequestHandler.class
			};
		}

		@Override
		protected String[] getScanableBindingPackages() {
			return new String[] {
					"com.tvd12.ezyfoxserver.support.test.entry"
			};
		}

		@Override
		protected void setupBeanContext(EzyAppContext context, EzyBeanContextBuilder builder) {
		}
		
	}
	
	public static class EzyAppEntryEx2 extends EzyDefaultAppEntry {

		@Override
		protected String[] getScanableBeanPackages() {
			return new String[0];
		}

		@Override
		protected String[] getScanableBindingPackages() {
			return new String[0];
		}

		@Override
		protected void setupBeanContext(EzyAppContext context, EzyBeanContextBuilder builder) {
		}
		
	}
	
}
