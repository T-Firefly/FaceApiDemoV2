package com.firefly.faceEngine.other;

import com.intellif.arctern.base.ArcternAttribute;
import com.intellif.arctern.base.ArcternImage;

public class FaceInfo {
    private ArcternImage arcternImage;
    private ArcternAttribute[][] attributes;
    private long searchId;

    public FaceInfo() {

    }

    public FaceInfo(ArcternImage arcternImage, ArcternAttribute[][] attributes) {
        this.arcternImage = arcternImage;
        this.attributes = attributes;
    }

    public long getFrameId() {
        if (arcternImage != null) {
            return arcternImage.frame_id;
        } else {
            return -1;
        }
    }

    public ArcternImage getArcternImage() {
        return arcternImage;
    }

    public void setArcternImage(ArcternImage arcternImage) {
        this.arcternImage = arcternImage;
    }

    public ArcternAttribute[][] getAttributes() {
        return attributes;
    }

    public void setAttributes(ArcternAttribute[][] attributes) {
        this.attributes = attributes;
    }

    public long getSearchId() {
        return searchId;
    }

    public void setSearchId(long searchId) {
        this.searchId = searchId;
    }

}
