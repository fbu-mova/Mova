package com.example.mova.model;

import com.example.mova.utils.StableNumericalIdProvider;
import com.parse.ParseObject;

public class HashableParseObject extends ParseObject {
    private static StableNumericalIdProvider idProvider = new StableNumericalIdProvider();

    @Override
    public int hashCode() {
        return idProvider.id(getObjectId());
    }
}
