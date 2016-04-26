package com.blaazinsoftware.centaur.data;

import com.blaazinsoftware.centaur.data.entity.AbstractEntity;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Ref;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Randy May
 *         Date: 15-09-22
 */
public class RefList<T extends AbstractEntity> extends ArrayList<Ref<T>> {

    public boolean add(T object) {
        return super.add(Ref.create(object));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<T> deRef() {
        return Lists.transform(this, new DeRef());
    }

    public class DeRef implements Function<Ref<T>, T> {
        @Override
        public T apply(Ref<T> reference) {
            return reference == null ? null : reference.get();
        }
    }
}
