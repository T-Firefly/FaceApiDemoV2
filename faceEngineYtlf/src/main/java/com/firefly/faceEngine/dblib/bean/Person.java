package com.firefly.faceEngine.dblib.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Person {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private byte[] feature;
    private String imgUrl;
    @Generated(hash = 2007404163)
    public Person(Long id, String name, byte[] feature, String imgUrl) {
        this.id = id;
        this.name = name;
        this.feature = feature;
        this.imgUrl = imgUrl;
    }
    @Generated(hash = 1024547259)
    public Person() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public byte[] getFeature() {
        return this.feature;
    }
    public void setFeature(byte[] feature) {
        this.feature = feature;
    }
    public String getImgUrl() {
        return this.imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", name='" + name + '\'';
    }

    public String toString2() {
        return ">>>>>>>>>>>>>>>>>>"+toString();
    }
}
