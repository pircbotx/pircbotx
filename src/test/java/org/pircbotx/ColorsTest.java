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

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 *
 */
public class ColorsTest {
	@Test
	public void lookupTableTest() {
		//Gather all the field names of the class
		Set<String> colorNames = new HashSet<>();
		for (Field curField : Colors.class.getFields())
			if (TestUtils.isRealMember(curField))
				colorNames.add(curField.getName());
		colorNames.remove("LOOKUP_TABLE");
		colorNames.remove("COLORS_TABLE");
		colorNames.remove("FORMATTING_TABLE");

		Sets.SetView<String> diff = Sets.symmetricDifference(colorNames, Colors.LOOKUP_TABLE.keySet());
		assertEquals(diff.size(), 0, "Missing keys in LOOKUP_TABLE: " + diff);
	}
}
