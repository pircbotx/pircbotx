/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.output;

import com.beust.jcommander.internal.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.TestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author leon
 */
@Test(singleThreaded = true)
public class OutputSplitTest {
	@Slf4j
	protected static class TestOutputPircBotX extends PircBotX {
		protected final Queue<String> outputQueue;
		@Getter
		protected boolean closed = false;

		public TestOutputPircBotX(Configuration configuration, Queue<String> outputQueue) {
			super(configuration);
			this.outputQueue = outputQueue;
		}

		@Override
		protected void sendRawLineToServer(String line) throws IOException {
			outputQueue.add(line);
		}

		@Override
		public boolean isConnected() {
			return true;
		}

		@Override
		public void close() {
			closed = true;
		}
	}
	protected static final int MAX_LINE_LENGTH = 20;
	protected TestOutputPircBotX bot;
	protected Queue<String> outputQueue;

	@BeforeMethod
	public void setup() {
		outputQueue = Lists.newLinkedList();

		Configuration config = TestUtils.generateConfigurationBuilder()
				.setAutoSplitMessage(true)
				.setMaxLineLength(MAX_LINE_LENGTH)
				.buildConfiguration();
		bot = new TestOutputPircBotX(config, outputQueue);
	}

	@Test
	public void sendRawLineSplitLongSimple() throws IOException {
		assertTrue(bot.getConfiguration().isAutoSplitMessage(), "Auto split not enabled");
		assertEquals(bot.getConfiguration().getMaxLineLength(), MAX_LINE_LENGTH, "Size is different");

		//Test strings
		String test1, test2, test3;
		List<String> testStrings = Arrays.asList(
				test1 = "hbdpopqgllyl",
				test2 = "nbmnqfttzczw",
				test3 = "hjnxgjwrtmcb"
		);
		for (String curTest : testStrings) {
			assertEquals(curTest.length(), MAX_LINE_LENGTH - 6/*ends*/ - 2/*Newline*/, "Test string length is wrong for " + curTest);
		}

		//Combine and send
		bot.sendRaw().rawLineSplit("BEG", StringUtils.join(testStrings, ""), "END");

		//Verify output
		checkOutput("BEG" + test1 + "END",
				"BEG" + test2 + "END",
				"BEG" + test3 + "END");
	}

	protected void checkOutput(String... expected) {
		int counter = 0;
		for (String curExpected : expected) {
			assertEquals(outputQueue.remove(), curExpected, "Failed to grab line " + (counter++));
		}
		assertTrue(outputQueue.isEmpty(), "Queue not empty, remaining: "
				+ SystemUtils.LINE_SEPARATOR + StringUtils.join(outputQueue, SystemUtils.LINE_SEPARATOR));
	}
}
