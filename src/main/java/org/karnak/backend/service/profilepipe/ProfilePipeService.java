/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.service.profilepipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.model.profilebody.ProfilePipeBody;
import org.karnak.frontend.profile.ProfileError;

public abstract class ProfilePipeService implements Serializable {

  public abstract List<ProfileEntity> getAllProfiles();

  public abstract ArrayList<ProfileError> validateProfile(ProfilePipeBody profilePipeYml);

  public abstract ProfileEntity saveProfilePipe(ProfilePipeBody profilePipeYml, Boolean byDefault);

  public abstract ProfileEntity updateProfile(ProfileEntity profileEntity);

  public abstract void deleteProfile(ProfileEntity profileEntity);
}
