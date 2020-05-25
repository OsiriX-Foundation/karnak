package org.karnak.profileschain;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.karnak.api.PseudonymApi;
import org.karnak.api.rqbody.Fields;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.ActionStrategy;
import org.karnak.profileschain.profilebody.ProfileBody;
import org.karnak.profileschain.profilebody.ProfileChainBody;
import org.karnak.profileschain.profiles.AbstractProfileItem;
import org.karnak.profileschain.profiles.ProfileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.util.StringUtil;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ProfileChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileChain.class);

    private final URL profileURL;
    private final Set<ProfileItem> sortedSet;
    private final ProfileChainBody profileChainYml;

    public ProfileChain(URL profileURL) {
        this.profileURL = profileURL;
        this.profileChainYml = init(profileURL);
        this.sortedSet = createProfileChain();
    }

    private ProfileChainBody init(URL profileURL) {
        try (InputStream inputStream = profileURL.openStream()) {
            final Yaml yaml = new Yaml(new Constructor(ProfileChainBody.class));
            return yaml.load(inputStream);
        } catch (final Exception e) {
            LOGGER.error("Cannot load yaml {}", profileURL, e);
        }
        return null;
    }

    public Set<ProfileItem> createProfileChain() {
        if (profileChainYml != null) {
            LinkedHashSet<ProfileItem> sortedSet = new LinkedHashSet<ProfileItem>();
            final List<ProfileBody> profilesYml = profileChainYml.getProfiles();
            for (ProfileBody profileYml : profilesYml) {
                AbstractProfileItem.Type t = AbstractProfileItem.Type.getType(profileYml.getCodename());
                if (t == null) {
                    LOGGER.error("Cannot find the profile codename: {}", profileYml.getCodename());
                } else {
                    try {
                        Object instance = t.getProfileClass().getConstructor(String.class, String.class).newInstance(profileYml.getName(), profileYml.getCodename());
                        sortedSet.add(t.getProfileClass().cast(instance));
                    } catch (Exception e) {
                        LOGGER.error("Cannot build the profile: {}", t.getProfileClass().getName());
                    }
                }
            }
            return sortedSet;
        }
        return Collections.emptySet();
    }

    public URL getProfileURL() {
        return profileURL;
    }

    public Set<ProfileItem> getSortedSet() {
        return sortedSet;
    }

    public String getMainzellistePseudonym(DicomObject dcm) {
        String patientID = dcm.getString(Tag.PatientID).orElse(null);
        String patientName = dcm.getString(Tag.PatientName).orElse(null);
        String patientBirthDate = dcm.getString(Tag.PatientBirthDate).orElse(null);
        String patientSex = dcm.getString(Tag.PatientSex).orElse(null);
        // Issuer of patientID is recommended to make the patientID universally unique. Can be defined in profile if missing.
        String issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(profileChainYml.getDefaultIssuerOfPatientID());

        PseudonymApi pseudonymApi = new PseudonymApi();
        Fields newPatientFields = new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
        String pseudonym = pseudonymApi.createPatient(newPatientFields);
        pseudonymApi.searchPatient(pseudonym);
        return pseudonym;
    }

    public static List<DicomObject> getSequence(DicomObject dicom, int tagSeq) {
        if (dicom != null) {
            Optional<DicomElement> item = dicom.get(tagSeq);
            if (item.isPresent() && !item.get().isEmpty() && item.get().vr() == VR.SQ) {
                return item.get().itemStream().collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public void apply(DicomObject dcm) {
        String pseudonym = getMainzellistePseudonym(dcm);
        if(!StringUtil.hasText(pseudonym)){
            throw new IllegalStateException("Cannot build a pseudonym");
        }

        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            DicomElement dcmEl = iterator.next();
            for (ProfileItem p : sortedSet) {
                Action action = p.getAction(dcmEl);
                if (action != null) {
                    try {
                        ActionStrategy.Output out = action.execute(dcm, dcmEl.tag(), pseudonym, null);
                        if (out == ActionStrategy.Output.TO_REMOVE) {
                            iterator.remove();
                        }
                    } catch (final Exception e) {
                        LOGGER.error("Cannot execute the action {}", action, e);
                    }
                    continue;
                }
            }
        }

        String profileFilename = profileChainYml.getName();
        dcm.setString(Tag.PatientID, VR.LO,  pseudonym);
        dcm.setString(Tag.PatientName, VR.LO,  pseudonym);
        dcm.setString(Tag.PatientIdentityRemoved, VR.CS,  "YES"); // TODO pixel data is missing
        dcm.setString(Tag.DeidentificationMethod, VR.LO,  sortedSet.stream().map(p -> p.getCodeName()).toArray(String[]::new));

        //dcm.setString(Tag.ClinicalTrialSponsorName, VR.LO, PiName);
        dcm.setString(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym);
        //       dcm.setString(Tag.ClinicalTrialProtocolID, VR.LO, hash(profileFilename));
    }

}
