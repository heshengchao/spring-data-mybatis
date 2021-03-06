/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mybatis.mapping.model;

import java.util.Locale;

import org.springframework.data.mybatis.dialect.Dialect;
import org.springframework.util.StringUtils;

/**
 * Models an identifier (name), which may or may not be quoted.
 *
 * @author JARVIS SONG
 * @since 2.0.0
 */
public class Identifier implements Comparable<Identifier> {

	private final String text;

	private final boolean isQuoted;

	public static Identifier toIdentifier(String text) {
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		final String trimmedText = text.trim();
		if (isQuoted(trimmedText)) {
			final String bareName = trimmedText.substring(1, trimmedText.length() - 1);
			return new Identifier(bareName, true);
		}
		else {
			return new Identifier(trimmedText, false);
		}
	}

	public static Identifier toIdentifier(String text, boolean quote) {
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		final String trimmedText = text.trim();
		if (isQuoted(trimmedText)) {
			final String bareName = trimmedText.substring(1, trimmedText.length() - 1);
			return new Identifier(bareName, true);
		}
		else {
			return new Identifier(trimmedText, quote);
		}
	}

	public static boolean isQuoted(String name) {
		return (name.startsWith("`") && name.endsWith("`")) || (name.startsWith("[") && name.endsWith("]"))
				|| (name.startsWith("\"") && name.endsWith("\""));
	}

	public Identifier(String text, boolean quoted) {
		if (StringUtils.isEmpty(text)) {
			throw new IllegalIdentifierException("Identifier text cannot be null");
		}
		if (isQuoted(text)) {
			throw new IllegalIdentifierException("Identifier text should not contain quote markers (` or \")");
		}
		this.text = text;
		this.isQuoted = quoted;
	}

	protected Identifier(String text) {
		this.text = text;
		this.isQuoted = false;
	}

	public String getText() {
		return this.text;
	}

	public boolean isQuoted() {
		return this.isQuoted;
	}

	public String render(Dialect dialect) {
		return this.isQuoted ? dialect.openQuote() + this.getText() + dialect.closeQuote() : this.getText();
	}

	public String render() {
		return this.isQuoted ? '`' + this.getText() + '`' : this.getText();
	}

	public String getCanonicalName() {
		return this.isQuoted ? this.text : this.text.toLowerCase(Locale.ENGLISH);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Identifier)) {
			return false;
		}

		final Identifier that = (Identifier) o;
		return this.getCanonicalName().equals(that.getCanonicalName());
	}

	@Override
	public int hashCode() {
		return this.isQuoted ? this.text.hashCode() : this.text.toLowerCase(Locale.ENGLISH).hashCode();
	}

	@Override
	public String toString() {
		return this.render();
	}

	public static boolean areEqual(Identifier id1, Identifier id2) {
		if (id1 == null) {
			return id2 == null;
		}
		else {
			return id1.equals(id2);
		}
	}

	public static Identifier quote(Identifier identifier) {
		return identifier.isQuoted() ? identifier : Identifier.toIdentifier(identifier.getText(), true);
	}

	@Override
	public int compareTo(Identifier o) {
		return this.getCanonicalName().compareTo(o.getCanonicalName());
	}

}
