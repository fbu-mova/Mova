package com.example.mova.utils;

import java.util.HashMap;
import java.util.Map;

/** @source https://stackoverflow.com/questions/38400641/is-there-some-recommended-way-to-create-an-id-for-a-parseobject-for-use-in-a-rec */
public class StableNumericalIdProvider {
    private int idProvider;
    private final Map<String, Integer> numericalIds = new HashMap<>();

    public int id(String stringId) {
        Integer numericalId = numericalIds.get(stringId);
        if (numericalId == null) {
            numericalId = idProvider++;
            numericalIds.put(stringId, numericalId);
        }
        return numericalId;
    }
}
