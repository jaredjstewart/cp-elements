/*
 * Copyright 2016 Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cp.elements.enums;

/**
 * The TimeUnit enum defines constants (enumerated values) for units of time.
 *
 * @author John J. Blum
 * @see java.lang.Enum
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public enum TimeUnit {
  NANOSECOND("ns", "Nanosecond", "1 billionth of a second"),
  MICROSECOND("us", "Microsecond", "1 millionth of a second"),
  MILLISECOND("ms", "Millisecond", "1 thousandth of a second"),
  SECOND("s", "Second", "1 second"),
  MINUTE("mi", "Minute", "60 seconds"),
  HOUR("hr", "Hour", "60 minutes"),
  DAY("day", "Day", "24 hours"),
  WEEK("wk", "Week", "7 days"),
  MONTH("mon", "Month", "28-31 days"),
  YEAR("yr", "Year", "12 months, 365 days"),
  DECADE("dec", "Decade", "10 years"),
  SCORE("Score", "Score", "20 years"),
  CENTURY("cent", "Century", "100 years"),
  MILLENIA("Millenia", "Millenia", "1000 years");

  private final String abbreviation;
  private final String description;
  private final String name;

  TimeUnit(final String abbreviation, final String name, final String description) {
    this.abbreviation = abbreviation;
    this.name = name;
    this.description = description;
  }

  public static TimeUnit valueOfAbbreviation(final String abbreviation) {
    for (TimeUnit unit : values()) {
      if (unit.getAbbreviation().equalsIgnoreCase(abbreviation)) {
        return unit;
      }
    }

    return null;
  }

  public static TimeUnit valueOfName(final String name) {
    for (TimeUnit unit : values()) {
      if (unit.getName().equalsIgnoreCase(name)) {
        return unit;
      }
    }

    return null;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
