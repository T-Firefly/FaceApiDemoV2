package com.firefly.faceEngine.other;

import com.firefly.arcterndemo.R;
import com.firefly.faceEngine.utils.Tools;
import com.intellif.arctern.base.ArcternAttribute;
import com.intellif.arctern.base.ArcternImage;

public class FaceInfo {
    private ArcternImage arcternImage;
    private ArcternAttribute[][] attributes;
    private long searchId;
    private float faceQualityConfidence = 0f;//人脸质量 范围0-1
    private float genderConfidence = 0f;//性别 范围0-1
    private float livenessConfidence; //活体 置信度
    private boolean isFaceMask; //口罩
    private int liveLabel; //活体  0未知，1活体，－1非活体
    private int gender; //性别  0未知，1男，2女
    private int age; //年龄

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

    public float getGenderConfidence() {
        return genderConfidence;
    }

    public void setGenderConfidence(float genderConfidence) {
        this.genderConfidence = genderConfidence;
    }

    public String getGenderString() {
        if (gender == 1) {
            return Tools.getString(R.string.ytlf_dictionaries47) + " ：" + genderConfidence;
        } else if (gender == 2) {
            return Tools.getString(R.string.ytlf_dictionaries48) + " ：" + genderConfidence;
        } else {
            return "--";
        }
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getAgeString() {
        if (age > 0) {
            return String.valueOf(age);
        } else {
            return "--";
        }
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

    public float getFaceQualityConfidence() {
        return faceQualityConfidence;
    }

    public void setFaceQualityConfidence(float faceQualityConfidence) {
        this.faceQualityConfidence = faceQualityConfidence;
    }

    public boolean isFaceMask() {
        return isFaceMask;
    }

    public void setFaceMask(boolean faceMask) {
        isFaceMask = faceMask;
    }

    public boolean isLiveness() {
        return liveLabel == 1;
    }

    public boolean isNotLiveness() {
        return liveLabel == -1;
    }

    public int getLiveLabel() {
        return liveLabel;
    }

    public void setLiveLabel(int liveLabel) {
        this.liveLabel = liveLabel;
    }

    public float getLivenessConfidence() {
        return livenessConfidence;
    }

    public void setLivenessConfidence(float livenessConfidence) {
        this.livenessConfidence = livenessConfidence;
    }
}
