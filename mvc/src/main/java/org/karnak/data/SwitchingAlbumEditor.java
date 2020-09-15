package org.karnak.data;

import org.dcm4che6.data.DicomObject;
import org.karnak.data.gateway.KheopsAlbums;
import org.karnak.kheops.SwitchingAlbum;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

import java.util.ArrayList;
import java.util.List;

public class SwitchingAlbumEditor implements AttributeEditor {
    private SwitchingAlbum switchingAlbum;
    private List<KheopsAlbums> listKheopsAlbums;

    public SwitchingAlbumEditor(KheopsAlbums kheopsAlbums) {
        this.switchingAlbum = new SwitchingAlbum();
        this.listKheopsAlbums = new ArrayList<>();
        this.listKheopsAlbums.add(kheopsAlbums);
    }

    public SwitchingAlbumEditor(List<KheopsAlbums> listKheopsAlbums) {
        this.switchingAlbum = new SwitchingAlbum();
        this.listKheopsAlbums = listKheopsAlbums;
    }

    @Override
    public void apply(DicomObject dcm, AttributeEditorContext context) {
        listKheopsAlbums.forEach(kheopsAlbums -> {
            switchingAlbum.apply(kheopsAlbums, dcm);
        });
    }
}
