/*
 * Copyright 2013 Alex Bogdanovski <alex@erudika.com>.
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
 *
 * You can reach the author at: https://github.com/albogdano
 */
package com.erudika.para.core;

import com.erudika.para.persistence.DAO;
import com.erudika.para.persistence.MockDAO;
import com.erudika.para.utils.Config;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Alex Bogdanovski <alex@erudika.com>
 */
public class SyspropTest {
	
	private static DAO dao;
	private static Sysprop s;
	
	@Before
	public void setUp() {
		dao = new MockDAO();
		s = new Sysprop("5");
		s.setDao(dao);
		s.addProperty("test1", "ok");
		s.addProperty("test2", "nope");
		s.addProperty("test3", "sure");
		s.addProperty("test4", false);
		s.addProperty("test5", 42);		
	}

	@Test
	public void testAddRemoveHasProperty() {
		s.setProperties(null);
		assertTrue(s.getProperties().isEmpty());
		s.addProperty(null, "asd");
		assertTrue(s.getProperties().isEmpty());
		s.addProperty("123", "123").addProperty("123", "1234").addProperty("", "123");
		assertTrue(s.getProperties().size() == 1);
		assertFalse(s.hasProperty(""));
		assertFalse(s.hasProperty(null));
		assertFalse(s.hasProperty("141"));
		assertTrue(s.hasProperty("123"));
		s.removeProperty("123");
		assertTrue(s.getProperties().isEmpty());		
	}

	@Test
	public void testGetProperty() {
		s.create();
		Sysprop s1 = dao.read(s.getId());
		assertEquals(s.getProperty("test5"), s1.getProperty("test5"));
		assertEquals(s.getProperty("test4"), s1.getProperty("test4"));
		assertEquals(s.getProperty("test3"), s1.getProperty("test3"));
		assertEquals(s.getProperty("test2"), s1.getProperty("test2"));
		assertEquals(s.getProperty("test1"), s1.getProperty("test1"));
		assertFalse((Boolean) s1.getProperty("test4"));
		assertTrue(s1.getProperty("test5") == 42);
	}

}