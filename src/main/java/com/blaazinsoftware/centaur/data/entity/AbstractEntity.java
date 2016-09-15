package com.blaazinsoftware.centaur.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Convenient base class for using Centaur.
 */
@Entity
public abstract class AbstractEntity implements Serializable {

    @Id
    @JsonIgnore
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (null == id) {
            return "null";
        }
        return "ID: " + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity) {
            AbstractEntity entity = (AbstractEntity) obj;
            return !(id == null || entity.getId() == null) && (id.equals(entity.getId()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        }
        return id.hashCode();
    }

    public String getWebSafeKey() {
        if (null == id) {
            return null;
        }
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Parent.class)) {
                Object parent = runGetter(field);
                Ref<?> reference = Ref.create(parent);
                return Key.create(reference.key(), this.getClass(), id).toWebSafeString();
            }
        }
        return Key.create(this.getClass(), id).toWebSafeString();
    }

    private Object runGetter(Field field) {
        // Find the correct method
        for (Method method : this.getClass().getMethods()) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3))) {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                    // Method found, run it
                    try {
                        return method.invoke(this);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }

        return null;
    }
}
