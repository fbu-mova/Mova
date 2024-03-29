package com.example.mova.model;

import androidx.annotation.Nullable;

import com.example.mova.utils.StableNumericalIdProvider;
import com.parse.ParseObject;

public class HashableParseObject extends ParseObject {
    private static StableNumericalIdProvider idProvider = new StableNumericalIdProvider();

    @Override
    public int hashCode() {
        return getHashCode(this);
    }

    /**
     * Returns the unique, stable hash code for a ParseObject.
     * Used for ParseObjects (like ParseUser) which cannot extend HashableParseObject.
     * All ParseObjects--including those that tap into this static method--use the same stable id provider.
     * @param object The ParseObject whose hash code should be calculated.
     * @return The ParseObject's hash code, using the global id provider.
     */
    public static int getHashCode(ParseObject object) {
        return idProvider.id(object.getObjectId());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return equals(this, obj);
    }

    public static boolean equals(ParseObject a, Object b) {
        if (b instanceof ParseObject) {
            return a.getObjectId().equals(((ParseObject) b).getObjectId());
        } else {
            return false;
        }
    }
}
