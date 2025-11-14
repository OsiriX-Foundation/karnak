/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import java.util.List;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.frontend.util.CollatorUtils;

public class GridForwardNode extends Grid<ForwardNodeEntity> {

	public GridForwardNode() {
		setSizeFull();

		Column<ForwardNodeEntity> forwardAeTitleColumn = addColumn(ForwardNodeEntity::getFwdAeTitle)
			.setHeader("Forward AETitle")
			.setFlexGrow(20)
			.setSortable(true)
			.setComparator(CollatorUtils.comparing(ForwardNodeEntity::getFwdAeTitle));

		addColumn(ForwardNodeEntity::getFwdDescription).setHeader("Description")
			.setFlexGrow(20)
			.setSortable(true)
			.setComparator(CollatorUtils.comparing(ForwardNodeEntity::getFwdDescription));

		GridSortOrder<ForwardNodeEntity> order = new GridSortOrder<>(forwardAeTitleColumn, SortDirection.ASCENDING);
		sort(List.of(order));
	}

	public ForwardNodeEntity getSelectedRow() {
		return asSingleSelect().getValue();
	}

	public void refresh(ForwardNodeEntity data) {
		getDataCommunicator().refresh(data);
	}

	public void selectRow(ForwardNodeEntity row) {
		getSelectionModel().select(row);
	}

}
