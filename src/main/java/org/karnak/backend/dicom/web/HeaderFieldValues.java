/*
 * Copyright (c) 2019-2020 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.weasis.core.util.StringUtil;

public class HeaderFieldValues {

  private int start = 0;
  private int end = 0;
  private int position = 0;
  private char[] chars = null;
  private List<HashMap<String, String>> values;

  public HeaderFieldValues(String respContentType) {
    values = parse(respContentType);
  }

  protected boolean hasCharacter() {
    return position < chars.length;
  }

  protected String parseValue() {
    start = position;
    end = position;

    char c;
    while (hasCharacter()) {
      c = chars[position];
      if (c == '=' || c == ';') {
        break;
      }
      end++;
      position++;
    }
    return getValue(false);
  }

  protected String parseQuotedValue() {
    start = position;
    end = position;

    boolean quoted = false;
    boolean charEscaped = false;
    char c;
    while (hasCharacter()) {
      c = chars[position];
      if (!quoted && c == ';') {
        break;
      }
      if (!charEscaped && c == '"') {
        quoted = !quoted;
      }
      charEscaped = (!charEscaped && c == '\\');
      end++;
      position++;
    }
    return getValue(true);
  }

  private String getValue(boolean quoted) {
    // Remove leading white spaces
    while ((start < end) && (Character.isWhitespace(chars[start]))) {
      start++;
    }
    // Remove trailing white spaces
    while ((end > start) && (Character.isWhitespace(chars[end - 1]))) {
      end--;
    }
    // Remove quotation marks if exists
    if (quoted && ((end - start) >= 2) && (chars[start] == '"') && (chars[end - 1] == '"')) {
      start++;
      end--;
    }
    String result = null;
    if (end > start) {
      result = new String(chars, start, end - start);
    }
    return result;
  }

  protected List<HashMap<String, String>> parse(String content) {
    List<HashMap<String, String>> hvals = new ArrayList<>();
    if (StringUtil.hasText(content)) {
      // Split except inside double quotes
      String[] elements = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
      for (String element : elements) {
        HashMap<String, String> params = new HashMap<>();
        hvals.add(params);

        this.chars = element.toCharArray();
        this.position = 0;

        while (hasCharacter()) {
          String name = parseValue();
          String value = null;
          if (hasCharacter() && (chars[position] == '=')) {
            position++;
            value = parseQuotedValue();
          }
          if (hasCharacter() && (chars[position] == ';')) {
            position++;
          }

          if (StringUtil.hasText(name)) {
            params.put(
                name.toLowerCase(), value); // NOSONAR hasText(name) already check if name is null
          }
        }
      }
    }
    return hvals;
  }

  public List<HashMap<String, String>> getValues() {
    return values;
  }

  public void setValues(List<HashMap<String, String>> values) {
    this.values = values;
  }

  public boolean hasKey(String key) {
    for (HashMap<String, String> map : values) {
      if (map.containsKey(key)) {
        return true;
      }
    }
    return false;
  }

  public String getValue(String key) {
    for (HashMap<String, String> map : values) {
      String val = map.get(key);
      if (StringUtil.hasText(val)) {
        return val;
      }
    }
    return null;
  }

  public List<String> getValues(String key) {
    List<String> list = new ArrayList<>();
    for (HashMap<String, String> map : values) {
      String val = map.get(key);
      if (StringUtil.hasText(val)) {
        list.add(val);
      }
    }
    return list;
  }
}
