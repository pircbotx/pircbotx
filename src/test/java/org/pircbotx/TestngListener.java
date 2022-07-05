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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 *
 */
public class TestngListener extends TestListenerAdapter {
	protected static boolean disableLogging = Boolean.valueOf(System.getProperty("pircbotx.disableTestDetail", "false"));

	@Override
	public void onTestStart(ITestResult result) {
		log("START", result);
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		onTestDone(tr);
		log("FAILURE", tr);
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		log("SKIPPED", tr);
		//We should never have skipped tests
		//Usually from a DataProvider or other listener throwing an exception
		tr.setStatus(ITestResult.FAILURE);
	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		onTestDone(tr);
		log("SUCCESS", tr);
	}

	private void onTestDone(ITestResult tr) {
		PircTestRunner activeInstance = PircTestRunner.THREAD_INSTANCE.get();
		if (activeInstance != null) {
			throw new RuntimeException("Test's PircTestRunner is not closed: " + tr.getMethod());
		}

	}

	@Override
	public void onFinish(ITestContext testContext) {
		System.out.println("--- TESTNG FINISHED ---");
	}

	protected void log(String status, ITestResult tr) {
		if (disableLogging) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append('[').append(status).append("] ");
		sb.append(tr.getTestClass().getName()).append(':').append(tr.getName()).append(" - ");

		//Add description
		if (StringUtils.isNotEmpty(tr.getMethod().getDescription())) {
			sb.append(tr.getMethod().getDescription());
		} else {
			sb.append("(no description)");
		}

		//Add params
		if (ArrayUtils.isNotEmpty(tr.getParameters())) {
			sb.append(" - Params: ").append(StringUtils.join(tr.getParameters(), ", "));
		}

		System.out.println(sb.toString());
	}
}
