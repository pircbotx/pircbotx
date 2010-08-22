/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pircbotx.User;

/**
 *
 * @author LordQuackstar
 */
public class PerformanceTest {
	public static void main(String[] args) {



		for (int i = 0; i < 7; i++) {
			final Map<String, User> theMap = addMap(new ConcurrentHashMap<String, User>());
			final CountDownLatch flag = new CountDownLatch(1);
			Runnable run = new Runnable() {
				public void run() {
					try {
						flag.await();
						for(User value : theMap.values())
							value.getHostmask();
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}

				}
			};
			ExecutorService threadPool = Executors.newCachedThreadPool();
			threadPool.submit(run);
			threadPool.submit(run);
			threadPool.submit(run);
			threadPool.submit(run);
			flag.countDown();
		}

	}

	public static Map<String, User> addMap(Map<String, User> aMap) {
		System.out.println("Using class " + aMap.getClass().getSimpleName());
		long start = System.currentTimeMillis();
		System.out.println("Starting adding at " + start);
		Random r = new Random();
		for (int i = 0; i < 15000; i++)
			aMap.put("str" + r.nextLong(), new User(null,"userName" + r.nextLong()));
		long end = System.currentTimeMillis();
		System.out.println("Finished adding at " + end);
		System.out.println("Diff: " + (end - start));
		return aMap;
	}
}
