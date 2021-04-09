/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.echo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import java.util.Objects;

@JacksonXmlRootElement(localName = "destinations")
public class DestinationEchos {

  private List<DestinationEcho> destinationEchos;

  public DestinationEchos(List<DestinationEcho> destinationEchos) {
    this.destinationEchos = destinationEchos;
  }

  @JacksonXmlProperty(localName = "destination")
  @JacksonXmlElementWrapper(useWrapping = false)
  public List<DestinationEcho> getDestinationEchos() {
    return destinationEchos;
  }

  public void setDestinationEchos(List<DestinationEcho> destinationEchos) {
    this.destinationEchos = destinationEchos;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DestinationEchos that = (DestinationEchos) o;
    return Objects.equals(destinationEchos, that.destinationEchos);
  }

  @Override
  public int hashCode() {
    return Objects.hash(destinationEchos);
  }
}
