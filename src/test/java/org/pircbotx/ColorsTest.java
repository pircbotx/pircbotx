/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.Set;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Leon
 */
public class ColorsTest {
	@Test
	public void lookupTableTest() {
		//Gather all the field names of the class
		Set<String> colorNames = Sets.newHashSet();
		for (Field curField : Colors.class.getFields()) {
			colorNames.add(curField.getName());
		}
		colorNames.remove("LOOKUP_TABLE");
		
		Sets.SetView<String> diff = Sets.symmetricDifference(colorNames, Colors.LOOKUP_TABLE.keySet());
		assertEquals(diff.size(), 0, "Missing keys in LOOKUP_TABLE: " + diff);
	}
}
