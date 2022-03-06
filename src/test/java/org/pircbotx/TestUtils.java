/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.delay.StaticReadonlyDelay;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.types.GenericEvent;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.collections.Lists;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.WarnStatus;

/**
 *
 */
public class TestUtils {
	public static final ExceptionStopperAppender exAppender;
	static {
		//when getting a logged exception, fail immediately
		Logger loggerRoot = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

		exAppender = new ExceptionStopperAppender();
		exAppender.setContext(loggerRoot.getLoggerContext());
		exAppender.start();
		loggerRoot.addAppender(exAppender);
	}

	@DataProvider
	public static Object[][] eventObjectDataProvider() throws IOException {
		return generateEventArguments(true, false);
	}

	@DataProvider
	public static Object[][] eventGenericDataProvider() throws IOException {
		return generateEventArguments(false, true);
	}

	@DataProvider
	public static Object[][] eventAllDataProvider() throws IOException {
		return generateEventArguments(true, true);
	}

	public static UserHostmask generateTestUserSourceHostmask(PircBotX bot) {
		return bot.getConfiguration().getBotFactory().createUserHostmask(bot, "SourceUser!~SomeTest@host.test");
	}

	public static User generateTestUserSource(PircBotX bot) {
		return bot.getUserChannelDao().createUser(generateTestUserSourceHostmask(bot));
	}

	public static UserHostmask generateTestUserOtherHostmask(PircBotX bot) {
		return bot.getConfiguration().getBotFactory().createUserHostmask(bot, "OtherUser!~SomeTest@host.test");
	}

	public static User generateTestUserOther(PircBotX bot) {
		return bot.getUserChannelDao().createUser(generateTestUserOtherHostmask(bot));
	}

	protected static Object[][] generateEventArguments(boolean includeEvents, boolean includeGeneric) throws IOException {
		ClassPath classPath = ClassPath.from(TestUtils.class.getClassLoader());
		ImmutableSet.Builder<ClassPath.ClassInfo> classesBuilder = ImmutableSet.builder();
		if (includeEvents)
			classesBuilder.addAll(classPath.getTopLevelClasses(VoiceEvent.class.getPackage().getName()));
		if (includeGeneric)
			classesBuilder.addAll(classPath.getTopLevelClasses(GenericEvent.class.getPackage().getName()));
		List<Object[]> argumentBuilder = Lists.newArrayList();
		for (ClassPath.ClassInfo curClassInfo : classesBuilder.build()) {
			Class<?> loadedClass = curClassInfo.load();
			if (GenericEvent.class.isAssignableFrom(loadedClass) && !loadedClass.equals(GenericEvent.class))
				argumentBuilder.add(new Object[]{loadedClass});
		}
		return argumentBuilder.toArray(new Object[0][]);
	}

	public static String getRootName(Class<?> aClass) {
		String name = aClass.getSimpleName();

		if (StringUtils.endsWith(name, "Event"))
			return name.split("Event")[0];
		else if (StringUtils.endsWith(name, "Listener"))
			return name.split("Listener")[0];

		//Can't get anything, error out
		throw new IllegalArgumentException("Cannot get root of class name " + aClass.toString());
	}

	public static Configuration.Builder generateConfigurationBuilder() {
		return new Configuration.Builder()
				//Core/required for PircTestRunner
				.addServer("127.1.1.1")
				.setListenerManager(new GenericListenerManager())
				.setName("TestBot")
				.setMessageDelay( new StaticReadonlyDelay(0) )
				.setShutdownHookEnabled(false)
				.setAutoReconnect(false)
				//Optional
				.setCapEnabled(false) //CAP tests can enable this
				;
	}

	public static class ExceptionStopperAppender extends AppenderBase<ILoggingEvent> {
		public boolean failOnException = true;
		public static String NAME = "PircBotX-Test-Exception-Stopper";
		static final int ALLOWED_REPEATS = 5;
		private boolean guard = false;
		private int statusRepeatCount = 0;

		public ExceptionStopperAppender() {
			setName(NAME);
		}

		@Override
		protected void append(ILoggingEvent eventObject) {
			ThrowableProxy throwProxy = (ThrowableProxy) eventObject.getThrowableProxy();
			if (!failOnException || throwProxy == null)
				return;
			throw new LogException("Captured logged exception " + throwProxy.getClassName()
					+ ": " + throwProxy.getMessage(), throwProxy.getThrowable());
		}

		/**
		 * Modified copy of AppenderBase.doAppend() with exception logging
		 * removed
		 *
		 * @param eventObject
		 */
		@Override
		public synchronized void doAppend(ILoggingEvent eventObject) {
			// WARNING: The guard check MUST be the first statement in the
			// doAppend() method.

			// prevent re-entry.
			if (guard)
				return;

			try {
				guard = true;

				if (!this.started) {
					if (statusRepeatCount++ < ALLOWED_REPEATS)
						addStatus(new WarnStatus(
								"Attempted to append to non started appender [" + name + "].",
								this));
					return;
				}

				if (getFilterChainDecision(eventObject) == FilterReply.DENY)
					return;

				// ok, we now invoke derived class' implementation of append
				this.append(eventObject);
			} finally {
				guard = false;
			}
		}
	}
	
	public static class LogException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public LogException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	public static boolean isRealMember(Member member) {
		return !member.isSynthetic() && !member.getName().startsWith("__");
	}
}
