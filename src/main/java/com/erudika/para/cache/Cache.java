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
package com.erudika.para.cache;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Alex Bogdanovski <alex@erudika.com>
 */
public interface Cache {
	
	public boolean contains(String id);
	public boolean contains(String appName, String id);
	
	public <T> void put(String id, T object);
	public <T> void put(String appName, String id, T object);

	public <T> void put(String appName, String id, T object, Long ttl_seconds);
	public <T> void putAll(Map<String, T> objects);
	public <T> void putAll(String appName, Map<String, T> objects);
	
	public <T> T get(String id);
	public <T> T get(String appName, String id);
	
	public <T> Map<String, T> getAll(List<String> ids);
	public <T> Map<String, T> getAll(String appName, List<String> ids);
	
	public void remove(String id);
	public void remove(String appName, String id);
	
	public void removeAll();
	public void removeAll(String appName);
	
	public void removeAll(List<String> ids);
	public void removeAll(String appName, List<String> ids);
	
}