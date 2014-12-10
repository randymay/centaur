package org.blaazinsoftware.centaur.service;

import org.blaazinsoftware.centaur.data.dto.AbstractIDEntity;

public class EntityWithAllFields extends AbstractIDEntity {
    private byte byteField;
    private Byte byteWrapperField;
    private int intField;
    private Integer integerWrapperField;
    private float floatField;
    private Float floatWrapperField;
    private double doubleField;
    private Double doubleWrapperField;

    public byte getByteField() {
        return byteField;
    }

    public void setByteField(byte byteField) {
        this.byteField = byteField;
    }

    public Byte getByteWrapperField() {
        return byteWrapperField;
    }

    public void setByteWrapperField(Byte byteWrapperField) {
        this.byteWrapperField = byteWrapperField;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public Integer getIntegerWrapperField() {
        return integerWrapperField;
    }

    public void setIntegerWrapperField(Integer integerWrapperField) {
        this.integerWrapperField = integerWrapperField;
    }

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }

    public Float getFloatWrapperField() {
        return floatWrapperField;
    }

    public void setFloatWrapperField(Float floatWrapperField) {
        this.floatWrapperField = floatWrapperField;
    }

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public Double getDoubleWrapperField() {
        return doubleWrapperField;
    }

    public void setDoubleWrapperField(Double doubleWrapperField) {
        this.doubleWrapperField = doubleWrapperField;
    }
}
