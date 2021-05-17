# Changelog

## [v0.9.6](https://github.com/OsiriX-Foundation/karnak/tree/v0.9.6) (2021-05-07)

[Full Changelog](https://github.com/OsiriX-Foundation/karnak/compare/v0.9.5...v0.9.6)

**Implemented enhancements:**

- Modal windows for the element of dicom worklist [\#135](https://github.com/OsiriX-Foundation/karnak/issues/135)
- Bump vaadin.version from 17.0.10 to 19.0.5 [\#133](https://github.com/OsiriX-Foundation/karnak/issues/133)
- remove the checkbox Authorized SOPs [\#97](https://github.com/OsiriX-Foundation/karnak/issues/97)
- Raise an exception from the execute function of an action. [\#73](https://github.com/OsiriX-Foundation/karnak/issues/73)
- \[Clean Pixel\] Recompression issue [\#39](https://github.com/OsiriX-Foundation/karnak/issues/39)

**Fixed bugs:**

- Decompress all the images with DICOM output [\#136](https://github.com/OsiriX-Foundation/karnak/issues/136)
- STOW-RS exceptions when sending images from multiple sources concurrently [\#124](https://github.com/OsiriX-Foundation/karnak/issues/124)

## [v0.9.5](https://github.com/OsiriX-Foundation/karnak/tree/v0.9.5) (2021-04-16)

[Full Changelog](https://github.com/OsiriX-Foundation/karnak/compare/v0.9.4...v0.9.5)

**Implemented enhancements:**

- Implement open id connect [\#112](https://github.com/OsiriX-Foundation/karnak/issues/112)

**Fixed bugs:**

- Exception loading sessions from persistent storage [\#127](https://github.com/OsiriX-Foundation/karnak/issues/127)

## [v0.9.4](https://github.com/OsiriX-Foundation/karnak/tree/v0.9.4) (2021-03-17)

[Full Changelog](https://github.com/OsiriX-Foundation/karnak/compare/v0.9.3.1...v0.9.4)

**Implemented enhancements:**

- Reorganization in the types of pseudonyms and improvement the management of pseudonyms [\#118](https://github.com/OsiriX-Foundation/karnak/issues/118)
- Display a unique message when we add multiple  same external pseudonym via the csv file [\#108](https://github.com/OsiriX-Foundation/karnak/issues/108)
- Linking external pseudonym to a project [\#107](https://github.com/OsiriX-Foundation/karnak/issues/107)
- Pagination to visualize the External Pseudonym view [\#106](https://github.com/OsiriX-Foundation/karnak/issues/106)

**Fixed bugs:**

- Manage exception if status code is not SUCCESSFUL for a dicom stow [\#117](https://github.com/OsiriX-Foundation/karnak/issues/117)
- Manage exception on parsing datetime [\#116](https://github.com/OsiriX-Foundation/karnak/issues/116)
- Incorrect trailing \(FFFE,E00D\) Item Delimitation Item in outgoing C-STORE RQs. [\#109](https://github.com/OsiriX-Foundation/karnak/issues/109)

## [v0.9.3.1](https://github.com/OsiriX-Foundation/karnak/tree/v0.9.3.1) (2021-02-05)

[Full Changelog](https://github.com/OsiriX-Foundation/karnak/compare/v0.9.3...v0.9.3.1)

**Fixed bugs:**

- Profile format is not correct when it exported [\#111](https://github.com/OsiriX-Foundation/karnak/issues/111)

## [v0.9.3](https://github.com/OsiriX-Foundation/karnak/tree/v0.9.3) (2021-02-01)

[Full Changelog](https://github.com/OsiriX-Foundation/karnak/compare/0.9.2...v0.9.3)

**Implemented enhancements:**

- Pop up warning when regenerate Project secret [\#92](https://github.com/OsiriX-Foundation/karnak/issues/92)
- Simplify the pseudonym cache metadata [\#90](https://github.com/OsiriX-Foundation/karnak/issues/90)
- In the UI, split the pseudonym cache and mainzelliste [\#89](https://github.com/OsiriX-Foundation/karnak/issues/89)
- Pass the action logs to level TRACE [\#84](https://github.com/OsiriX-Foundation/karnak/issues/84)
- Remove all references to the secret KARNAK\_HMAC\_KEY [\#81](https://github.com/OsiriX-Foundation/karnak/issues/81)
- Add Instance Creation Date and Time attributes [\#79](https://github.com/OsiriX-Foundation/karnak/issues/79)
- Update help view [\#78](https://github.com/OsiriX-Foundation/karnak/issues/78)
- Add rolling log for clinical log [\#77](https://github.com/OsiriX-Foundation/karnak/issues/77)
- Remove a profile [\#76](https://github.com/OsiriX-Foundation/karnak/issues/76)
- Improve the performance of the logs [\#67](https://github.com/OsiriX-Foundation/karnak/issues/67)
- Show masks in the profiles page [\#61](https://github.com/OsiriX-Foundation/karnak/issues/61)
- Export a profile [\#60](https://github.com/OsiriX-Foundation/karnak/issues/60)
- Reformat the code with google-java-format [\#40](https://github.com/OsiriX-Foundation/karnak/issues/40)
- Upgrade of version [\#23](https://github.com/OsiriX-Foundation/karnak/issues/23)
- Use of the DICOM structure according to Information Object Definition \(IOD\) [\#11](https://github.com/OsiriX-Foundation/karnak/issues/11)

**Fixed bugs:**

-  Upload profile error on Chrome [\#93](https://github.com/OsiriX-Foundation/karnak/issues/93)
- Missing level in log file [\#68](https://github.com/OsiriX-Foundation/karnak/issues/68)
- Hazelcast ClassNotFoundException in docker environment [\#66](https://github.com/OsiriX-Foundation/karnak/issues/66)

**Closed issues:**

- Logs the following fields for each de-identified instance in clinical logs \(Serie UID, Instance UID, ...\) [\#83](https://github.com/OsiriX-Foundation/karnak/issues/83)
- Add mask view in profile view [\#71](https://github.com/OsiriX-Foundation/karnak/issues/71)
-  Creation of a website for the karnak doc [\#70](https://github.com/OsiriX-Foundation/karnak/issues/70)

## [0.9.2](https://github.com/OsiriX-Foundation/karnak/tree/0.9.2) (2020-11-04)

[Full Changelog](https://github.com/OsiriX-Foundation/karnak/compare/0.9.1...0.9.2)

**Implemented enhancements:**

-  Reduce information in clinical log [\#56](https://github.com/OsiriX-Foundation/karnak/issues/56)
- Using caching for the Mainzelliste pseudonym [\#53](https://github.com/OsiriX-Foundation/karnak/issues/53)
- Improve the performance of pseudonym caching [\#52](https://github.com/OsiriX-Foundation/karnak/issues/52)
- Optimisation in actions [\#45](https://github.com/OsiriX-Foundation/karnak/issues/45)
- Upgrade Spring and Vaadin [\#41](https://github.com/OsiriX-Foundation/karnak/issues/41)
- Use DICOM endpoint as URL [\#36](https://github.com/OsiriX-Foundation/karnak/issues/36)
- Warning No projects created [\#35](https://github.com/OsiriX-Foundation/karnak/issues/35)
- Option shift date, keep year, month or day [\#30](https://github.com/OsiriX-Foundation/karnak/issues/30)
- Expression in replace [\#28](https://github.com/OsiriX-Foundation/karnak/issues/28)
- UI navigation [\#24](https://github.com/OsiriX-Foundation/karnak/issues/24)
- Switching in different KHEOPS' album [\#16](https://github.com/OsiriX-Foundation/karnak/issues/16)
- Implement clean pixel data \(text\) [\#14](https://github.com/OsiriX-Foundation/karnak/issues/14)
- Import an external pseudonym [\#13](https://github.com/OsiriX-Foundation/karnak/issues/13)
- Terms/Conditions on login page [\#8](https://github.com/OsiriX-Foundation/karnak/issues/8)

**Fixed bugs:**

- Validator applied to null fields after adding a new project [\#59](https://github.com/OsiriX-Foundation/karnak/issues/59)
- The project grid doesn't update after updating a project [\#58](https://github.com/OsiriX-Foundation/karnak/issues/58)
- What happens with a null UID [\#57](https://github.com/OsiriX-Foundation/karnak/issues/57)
- Define an "all" log file size [\#55](https://github.com/OsiriX-Foundation/karnak/issues/55)
- Sequences are not fully removed with Action X [\#51](https://github.com/OsiriX-Foundation/karnak/issues/51)
- Image with Clean pixel data is not sent to a DICOMWeb destination [\#50](https://github.com/OsiriX-Foundation/karnak/issues/50)
- DICOM connection between the forward node and the destination is closed randomly [\#49](https://github.com/OsiriX-Foundation/karnak/issues/49)
- Applying masks doesn't work when sending simultaneously several dataset [\#48](https://github.com/OsiriX-Foundation/karnak/issues/48)
- Notification for a DICOM destination doesn't work [\#47](https://github.com/OsiriX-Foundation/karnak/issues/47)
- Notification is not consistent when UIDs are changed by de-identification [\#46](https://github.com/OsiriX-Foundation/karnak/issues/46)
- NPE when setting a string value to Binary VR [\#44](https://github.com/OsiriX-Foundation/karnak/issues/44)
- Propagation of the action in sequence [\#29](https://github.com/OsiriX-Foundation/karnak/issues/29)
- Changing parameters during sending [\#26](https://github.com/OsiriX-Foundation/karnak/issues/26)

## [0.9.1](https://github.com/OsiriX-Foundation/karnak/tree/0.9.1) (2020-09-07)

[Full Changelog](https://github.com/OsiriX-Foundation/karnak/compare/0.9.0...0.9.1)

**Implemented enhancements:**

- Verification and error checking on the structure of the yaml file containing the profile [\#25](https://github.com/OsiriX-Foundation/karnak/issues/25)
- Expression in option or profile [\#22](https://github.com/OsiriX-Foundation/karnak/issues/22)
- Refactor constructor of ProfileItem [\#21](https://github.com/OsiriX-Foundation/karnak/issues/21)
- Option with Date [\#19](https://github.com/OsiriX-Foundation/karnak/issues/19)
- Exception type for destination [\#4](https://github.com/OsiriX-Foundation/karnak/issues/4)

## [0.9.0](https://github.com/OsiriX-Foundation/karnak/tree/0.9.0) (2020-07-24)

[Full Changelog](https://github.com/OsiriX-Foundation/karnak/compare/a8c8ee12a688add4caef2097ce5cc7c115180fbb...0.9.0)

**Implemented enhancements:**

- Import customize profile [\#20](https://github.com/OsiriX-Foundation/karnak/issues/20)
- Structure Profile [\#12](https://github.com/OsiriX-Foundation/karnak/issues/12)
- Filter by SOP [\#10](https://github.com/OsiriX-Foundation/karnak/issues/10)
- Configuration of user/password Admin [\#7](https://github.com/OsiriX-Foundation/karnak/issues/7)
- Vaadin Upgrade to v16 [\#6](https://github.com/OsiriX-Foundation/karnak/issues/6)
- Select all filters [\#5](https://github.com/OsiriX-Foundation/karnak/issues/5)



\* *This Changelog was automatically generated by [github_changelog_generator](https://github.com/github-changelog-generator/github-changelog-generator)*
