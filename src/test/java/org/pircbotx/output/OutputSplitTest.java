/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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
package org.pircbotx.output;

import org.pircbotx.TestPircBotX;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pircbotx.Configuration;
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
	protected static final int MAX_LINE_LENGTH = 70;
	protected TestPircBotX bot;
	protected Queue<String> outputQueue;

	@BeforeMethod
	public void setup() {
		Configuration.Builder config = TestUtils.generateConfigurationBuilder()
				.setAutoSplitMessage(true)
				.setMaxLineLength(MAX_LINE_LENGTH);
		bot = new TestPircBotX(config);
		outputQueue = bot.outputQueue;
	}

	@Test
	public void sendRawLineSplit() throws IOException {
		assertTrue(bot.getConfiguration().isAutoSplitMessage(), "Auto split not enabled");
		assertEquals(bot.getConfiguration().getMaxLineLength(), MAX_LINE_LENGTH, "Size is different");

		//Test strings
		List<String> testStrings = Arrays.asList(
				"bktwbljrgfmgsmvgaeqqpdcifkzgahlacilinoaufudgslavywgmuydysasdyg",
				"ktmqoxzdudvexesxenqhwmrcqgaouahmrqlbvtbixtwfhevxsdptbbgtzbpbme",
				"ckxavfsouqrdzugtmemxexwjdlufahcnlekbootyhpazkqciughdapgaxvcoze"
		);
		for (String curTest : testStrings) {
			assertEquals(curTest.length(), MAX_LINE_LENGTH - 6/*prefix+suffix*/ - 2/*Newline*/, "Test string length is wrong for " + curTest);
		}

		//Combine and send
		bot.sendRaw().rawLineSplit("BEG", StringUtils.join(testStrings, ""), "END");

		//Verify output
		List<String> expectedStrings = Lists.newArrayList();
		for (String curTestString : testStrings) {
			expectedStrings.add("BEG" + curTestString + "END");
		}
		checkOutput(expectedStrings.toArray(new String[]{}));
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
