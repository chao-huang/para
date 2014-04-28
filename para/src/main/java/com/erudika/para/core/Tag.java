/*
 * Copyright 2013-2014 Erudika. http://erudika.com
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
 * For issues and patches go to: https://github.com/erudika
 */
package com.erudika.para.core;

import com.erudika.para.annotations.Locked;
import com.erudika.para.annotations.Stored;
import com.erudika.para.utils.Config;
import com.erudika.para.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

/**
 * A tag. Must not be null or empty.
 * @author Alex Bogdanovski [alex@erudika.com]
 */
public class Tag extends PObject {
	private static final long serialVersionUID = 1L;
	private static final String prefix = Utils.type(Tag.class).concat(Config.SEPARATOR);

	@Stored @NotBlank @Locked private String tag;
	@Stored private Integer count;

	/**
	 * No-args constructor
	 */
	public Tag() {
		this(null);
	}

	/**
	 * Default constructor
	 * @param tag the tag
	 */
	public Tag(String tag) {
		this.count = 0;
		setTag(tag);
		setName(getName());
	}

	@Override
	public void setId(String id) {
		if (StringUtils.isBlank(id) || StringUtils.startsWith(id, prefix)) {
			super.setId(id);
		} else {
			setTag(id);
			super.setId(prefix.concat(getTag()));
		}
	}

	@Override
	public String getObjectURI() {
		String defurl = "/".concat(getPlural());
		return (getTag() != null) ? defurl.concat("/").concat(getTag()) : defurl;
	}

	/**
	 * The number of objects tagged with this tag.
	 * @return the number of times this tag is used
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * Sets the number of objects tagged with this tag.
	 * @param count a new count
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * The tag value.
	 * @return the tag itself
	 */
	public final String getTag() {
		return tag;
	}

	/**
	 * Sets the tag value. 
	 * @param tag a tag. Must not be null or empty.
	 */
	public final void setTag(String tag) {
		this.tag = Utils.noSpaces(tag, "-");
		if (!this.tag.isEmpty()) {
			setId(prefix.concat(getTag()));
		}
	}

	/**
	 * Increments the count when a new object is tagged.
	 */
	public void incrementCount() {
		this.count++;
	}

	/**
	 * Decrements the count when a new object is untagged.
	 */
	public void decrementCount() {
		this.count--;
		if (this.count < 1 && exists()) {
			delete();
		}
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Tag other = (Tag) obj;
		if (!StringUtils.equalsIgnoreCase(tag, other.tag)) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + (this.tag != null ? this.tag.hashCode() : 0);
		return hash;
	}
}