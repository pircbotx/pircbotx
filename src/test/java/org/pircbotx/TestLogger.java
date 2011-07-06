/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PircBotX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import java.lang.reflect.Method;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class TestLogger extends TestListenerAdapter {

	@Override
	public void onTestStart(ITestResult result) {
		log("START", result);
	}
	 
	
	@Override
	public void onTestFailure(ITestResult tr) {
		log("FAILURE", tr);
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		log("SKIPPED", tr);
	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		log("SUCCESS", tr);
	}

	protected void log(String status, ITestResult tr) {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(status).append("] ");
		sb.append(tr.getTestClass().getName()).append(":").append(tr.getName()).append(" - ");
		
		//Add description
		if (StringUtils.isNotEmpty(tr.getMethod().getDescription()))
			sb.append(tr.getMethod().getDescription());
		else
			sb.append("(no description)");
		
		//Add params
		if(ArrayUtils.isNotEmpty(tr.getParameters()))
			sb.append(" - Params: ").append(StringUtils.join(tr.getParameters(), ", "));
		
		System.out.println(sb.toString());
	}
}
