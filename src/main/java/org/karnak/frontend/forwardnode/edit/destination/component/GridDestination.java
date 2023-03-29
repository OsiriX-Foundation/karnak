/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit.destination.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import java.util.HashMap;
import java.util.Map;
import org.karnak.backend.data.entity.DestinationEntity;

public class GridDestination extends Grid<DestinationEntity> {

	// Forward node / Id / Image
	private Map<String, Map<Long, Image>> loadingImages = new HashMap<>();

	public GridDestination() {
		setSizeFull();

		addColumn(DestinationEntity::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);

		addColumn(DestinationEntity::getDestinationType).setHeader("Type").setFlexGrow(20).setSortable(true);

		addComponentColumn(destination -> {
			Span spanDot = new Span();
			spanDot.getStyle().set("height", "25px");
			spanDot.getStyle().set("width", "25px");
			spanDot.getStyle().set("border-radius", "50%");
			spanDot.getStyle().set("display", "inline-block");
			if (destination.isActivate()) {
				spanDot.getStyle().set("background-color", "#5FC04C");
			}
			else {
				spanDot.getStyle().set("background-color", "#FC4848");
			}
			return spanDot;
		}).setHeader("Enabled").setFlexGrow(20).setSortable(true);

		// Loading image
		addComponentColumn(this::buildLoadingImage).setHeader("Activity").setKey("Activity").setFlexGrow(20);
	}

	/**
	 * Build loading image
	 * @param destinationEntity Destination
	 * @return Loading image built
	 */
	private LoadingImage buildLoadingImage(DestinationEntity destinationEntity) {
		// Build loading image
		LoadingImage image = new LoadingImage("In progress", "10%");

		// Fill loading image map
		if (loadingImages.isEmpty()
				|| !loadingImages.containsKey(destinationEntity.getForwardNodeEntity().getFwdAeTitle())) {
			HashMap<Long, Image> loadingImagesMap = new HashMap<>();
			loadingImagesMap.put(destinationEntity.getId(), image);
			loadingImages.put(destinationEntity.getForwardNodeEntity().getFwdAeTitle(), loadingImagesMap);
		}
		else {
			loadingImages.get(destinationEntity.getForwardNodeEntity().getFwdAeTitle())
				.put(destinationEntity.getId(), image);
		}

		// Visibility
		image.setVisible(false);

		return image;
	}

	public DestinationEntity getSelectedRow() {
		return asSingleSelect().getValue();
	}

	public void refresh(DestinationEntity data) {
		getDataCommunicator().refresh(data);
	}

	public Map<String, Map<Long, Image>> getLoadingImages() {
		return loadingImages;
	}

	public void setLoadingImages(Map<String, Map<Long, Image>> loadingImages) {
		this.loadingImages = loadingImages;
	}

}
