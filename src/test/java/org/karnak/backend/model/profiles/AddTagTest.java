/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.profiles;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.karnak.backend.data.entity.IncludedTagEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.Profile;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AddTagTest {

    @Test
    void addTag() {
        ProfileEntity profileEntity = new ProfileEntity();
        Attributes attributes = new Attributes();
        attributes.setString(Tag.Modality, VR.CS, "XA");
        attributes.setString(Tag.SOPClassUID, VR.UI, "1.2.840.10008.5.1.4.1.1.12.1");

        Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
        ProfileElementEntity profileElementEntityAddBurnedAttr = new ProfileElementEntity();
        profileElementEntityAddBurnedAttr.setCodename("action.add.tag");
        profileElementEntityAddBurnedAttr.setName("Add tag BurnedInAnnotation");
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("value", "YES", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("vr", "CS", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addIncludedTag(new IncludedTagEntity("(0028,0301)", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.setPosition(1);

        profileElementEntities.add(profileElementEntityAddBurnedAttr);
        profileEntity.setProfileElementEntities(profileElementEntities);
        Profile profile = new Profile(profileEntity);

        // Apply the profile that adds the BurnedInAnnotation attribute to both objects
        profile.applyAction(attributes, attributes, null, null, null, null);

        // The BurnedInAnnotation attribute is added and its value set to YES
        assertEquals("YES", attributes.getString(Tag.BurnedInAnnotation));
    }

    @Test
    void addTag_withoutVR() {
        ProfileEntity profileEntity = new ProfileEntity();
        Attributes attributes = new Attributes();
        attributes.setString(Tag.Modality, VR.CS, "XA");
        attributes.setString(Tag.SOPClassUID, VR.UI, "1.2.840.10008.5.1.4.1.1.12.1");

        Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
        ProfileElementEntity profileElementEntityAddBurnedAttr = new ProfileElementEntity();
        profileElementEntityAddBurnedAttr.setCodename("action.add.tag");
        profileElementEntityAddBurnedAttr.setName("Add tag BurnedInAnnotation");
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("value", "YES", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addIncludedTag(new IncludedTagEntity("(0028,0301)", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.setPosition(1);

        profileElementEntities.add(profileElementEntityAddBurnedAttr);
        profileEntity.setProfileElementEntities(profileElementEntities);
        Profile profile = new Profile(profileEntity);

        // Apply the profile that adds the BurnedInAnnotation attribute to both objects
        profile.applyAction(attributes, attributes, null, null, null, null);

        // The BurnedInAnnotation attribute is added and its value set to YES
        assertEquals("YES", attributes.getString(Tag.BurnedInAnnotation));
        assertEquals("CS", attributes.getVR(Tag.BurnedInAnnotation).toString());
    }

    @Test
    void addTagThenIgnoreAction() {
        ProfileEntity profileEntity = new ProfileEntity();
        Attributes attributes = new Attributes();
        attributes.setString(Tag.Modality, VR.CS, "XA");
        attributes.setString(Tag.SOPClassUID, VR.UI, "1.2.840.10008.5.1.4.1.1.12.1");

        Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
        ProfileElementEntity profileElementEntityAddBurnedAttr = new ProfileElementEntity();
        profileElementEntityAddBurnedAttr.setCodename("action.add.tag");
        profileElementEntityAddBurnedAttr.setName("Add tag BurnedInAnnotation");
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("value", "YES", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("vr", "CS", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addIncludedTag(new IncludedTagEntity("(0028,0301)", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.setPosition(1);

        ProfileElementEntity profileElementEntitySetBurnedAttr = new ProfileElementEntity();
        profileElementEntitySetBurnedAttr.setCodename("expression.on.tags");
        profileElementEntitySetBurnedAttr.setName("Set tag BurnedInAnnotation to NO");
        profileElementEntitySetBurnedAttr.addArgument(new ArgumentEntity("expr", "Replace('NO')", profileElementEntityAddBurnedAttr));
        profileElementEntitySetBurnedAttr.addIncludedTag(new IncludedTagEntity("(0028,0301)", profileElementEntityAddBurnedAttr));
        profileElementEntitySetBurnedAttr.setPosition(2);

        profileElementEntities.add(profileElementEntityAddBurnedAttr);
        profileElementEntities.add(profileElementEntitySetBurnedAttr);
        profileEntity.setProfileElementEntities(profileElementEntities);
        Profile profile = new Profile(profileEntity);

        // Apply the profile that adds the BurnedInAnnotation attribute to both objects
        profile.applyAction(attributes, attributes, null, null, null, null);

        // The BurnedInAnnotation attribute is added and its value set to YES, the Replace is not applied
        assertEquals("YES", attributes.getString(Tag.BurnedInAnnotation));
    }

    @Test
    void addTag_ignoreTagAlreadyExisting() {
        ProfileEntity profileEntity = new ProfileEntity();
        Attributes attributes = new Attributes();
        attributes.setString(Tag.Modality, VR.CS, "XA");
        attributes.setString(Tag.SOPClassUID, VR.UI, "1.2.840.10008.5.1.4.1.1.12.1");
        attributes.setString(Tag.BurnedInAnnotation, VR.CS, "NO");

        Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
        ProfileElementEntity profileElementEntityAddBurnedAttr = new ProfileElementEntity();
        profileElementEntityAddBurnedAttr.setCodename("action.add.tag");
        profileElementEntityAddBurnedAttr.setName("Add tag BurnedInAnnotation");
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("value", "YES", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("vr", "CS", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addIncludedTag(new IncludedTagEntity("(0028,0301)", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.setPosition(1);

        profileElementEntities.add(profileElementEntityAddBurnedAttr);
        profileEntity.setProfileElementEntities(profileElementEntities);
        Profile profile = new Profile(profileEntity);

        // Apply the profile that adds the BurnedInAnnotation attribute to both objects
        profile.applyAction(attributes, attributes, null, null, null, null);

        // The add action is ignored because it already exists and its value is NO
        assertEquals("NO", attributes.getString(Tag.BurnedInAnnotation));
    }

    @Test
    void addTag_ignoreTagAlreadyExistingThenModify() {
        ProfileEntity profileEntity = new ProfileEntity();
        Attributes attributes = new Attributes();
        attributes.setString(Tag.Modality, VR.CS, "XA");
        attributes.setString(Tag.SOPClassUID, VR.UI, "1.2.840.10008.5.1.4.1.1.12.1");
        attributes.setString(Tag.BurnedInAnnotation, VR.CS, "");

        Set<ProfileElementEntity> profileElementEntities = new HashSet<>();
        ProfileElementEntity profileElementEntityAddBurnedAttr = new ProfileElementEntity();
        profileElementEntityAddBurnedAttr.setCodename("action.add.tag");
        profileElementEntityAddBurnedAttr.setName("Add tag BurnedInAnnotation");
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("value", "YES", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addArgument(new ArgumentEntity("vr", "CS", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.addIncludedTag(new IncludedTagEntity("(0028,0301)", profileElementEntityAddBurnedAttr));
        profileElementEntityAddBurnedAttr.setPosition(1);

        ProfileElementEntity profileElementEntitySetBurnedAttr = new ProfileElementEntity();
        profileElementEntitySetBurnedAttr.setCodename("expression.on.tags");
        profileElementEntitySetBurnedAttr.setName("Set tag BurnedInAnnotation to NO");
        profileElementEntitySetBurnedAttr.addArgument(new ArgumentEntity("expr", "Replace('NO')", profileElementEntityAddBurnedAttr));
        profileElementEntitySetBurnedAttr.addIncludedTag(new IncludedTagEntity("(0028,0301)", profileElementEntityAddBurnedAttr));
        profileElementEntitySetBurnedAttr.setPosition(2);

        profileElementEntities.add(profileElementEntityAddBurnedAttr);
        profileElementEntities.add(profileElementEntitySetBurnedAttr);
        profileEntity.setProfileElementEntities(profileElementEntities);
        Profile profile = new Profile(profileEntity);

        // Apply the profile that adds the BurnedInAnnotation attribute to both objects
        profile.applyAction(attributes, attributes, null, null, null, null);

        // The Add action is ignored, the Replace action sets the value to NO
        assertEquals("NO", attributes.getString(Tag.BurnedInAnnotation));
    }
}
