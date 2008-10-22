/*
 * Copyright 2008 Nokia Siemens Networks Oyj
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.robotframework.jvmconnector.server;

import org.jmock.MockObjectTestCase;
import org.robotframework.jvmconnector.common.PropertyParsingFailedException;
import org.robotframework.jvmconnector.server.PropertyParser;
import org.springframework.beans.PropertyValue;


public class PropertyParserTest extends MockObjectTestCase {
	private String propertyString = "property1=value1|property2=value2";
	private PropertyParser propertyParser;
	private String[] propertiesAsString;

	public void setUp() {
		propertyParser = new PropertyParser(propertyString);
		propertiesAsString = propertyString.split("\\|");
	}

	public void testParsesPropertiesIntoAnPropertyArrayOfCorrectLength() {
		assertTrue(propertiesAsString.length == propertyParser.getPropertyValues().length);
	}

	public void testReturnsArrayOfPropertyValuesFromGivenPropertyString() {
		assertPropertyValuesMatchWithThePropertyString(propertyString, propertyParser.getPropertyValues());
	}

	public void testThrowsPropertyParsingFailedWithBadPropertyPatterns() {
		PropertyParser parserWithBadPattern = new PropertyParser("bad pattern");
		try {
			parserWithBadPattern.getPropertyValues();
		} catch (PropertyParsingFailedException e) {
			return;
		}
		fail("Excpected PropertyParsingFailedException to be thrown");
	}

	private void assertPropertyValuesMatchWithThePropertyString(String propertyString, PropertyValue[] propertyValues) {
		final int NAME = 0;
		final int VALUE = 1;

		for (int i = 0; i < propertyValues.length; ++i) {
			String[] propertyAsString = propertiesAsString[i].split("=");

			assertEquals(propertyAsString[NAME], propertyValues[i].getName());
			assertEquals(propertyAsString[VALUE], propertyValues[i].getValue());
		}
	}
}
