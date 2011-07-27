package com.iCo6.IO.mini;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Arguments {
    private String key;
    private LinkedHashMap<String, String> values;
    private boolean caseSensitive;

    /**
     * Create a new value-mapped database entry.
     *
     * @param key Current line Entry Key
     */
    public Arguments(Object key) {
        this.key = parseKey(key);
        this.values = new LinkedHashMap<String, String>();
        this.caseSensitive = false;
    }

    public Arguments(Object key, boolean caseSensitive) {
        this.key = parseKey(key);
        this.values = new LinkedHashMap<String, String>();
        this.caseSensitive = caseSensitive;
    }

    public String getKey() {
        return parseKey(this.key);
    }

    private String encode(String data) {
        return data.trim().replace(" ", Dict.SPACE_SPLIT);
    }

    private String decode(String data) {
        return data.replace(Dict.SPACE_SPLIT, " ").trim();
    }

    public boolean hasKey(Object key) {
        return this.values.containsKey(parseKey(key));
    }

    public void setValue(String key, Object value) {
        this.values.put(this.encode(parseKey(key)), this.encode(String.valueOf(value)));
    }

    public String getValue(String key) {
        return this.decode(this.values.get(parseKey(key)));
    }

    public Integer getInteger(String key) throws NumberFormatException {
        return Integer.valueOf(this.getValue(key));
    }

    public Double getDouble(String key) throws NumberFormatException {
        return Double.valueOf(this.getValue(key));
    }

    public Long getLong(String key) throws NumberFormatException {
        return Long.valueOf(this.getValue(key));
    }

    public Float getFloat(String key) throws NumberFormatException {
        return Float.valueOf(this.getValue(key));
    }

    public Short getShort(String key) throws NumberFormatException {
        return Short.valueOf(this.getValue(key));
    }

    public Boolean getBoolean(String key) {
        return Boolean.valueOf(this.getValue(key));
    }

    public String[] getArray(String key) {
        String value = this.getValue(key);

        if(value == null || !value.contains(Dict.ARRAY_SPLIT)) return null;
        if(value.split(Dict.ARRAY_SPLIT) == null) return null;

        return this.trim(value.split(Dict.ARRAY_SPLIT));
    }

    private String[] trim(String[] values) {
        for (int i = 0, length = values.length; i < length; i++)
            if (values[i] != null)
                values[i] = values[i].trim();

        return values;
    }

    private String parseKey(Object key) {
        if(this.caseSensitive)
            return String.valueOf(key);

        return String.valueOf(key).toLowerCase();
    }

    private List trim(List values) {
        List trimmed = new ArrayList();

        for (int i = 0, length = values.size(); i < length; i++) {
            String v = (String) values.get(i);

            if (v != null) v = v.trim();

            trimmed.add(v);
        }

        return trimmed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.key).append(Dict.SPACER);

        for(String k: this.values.keySet())
            sb.append(k).append(Dict.ARGUMENT_SPLIT).append(this.values.get(k)).append(Dict.SPACER);

        return sb.toString().trim();
    }

    public Arguments copy() {
        Arguments copy = new Arguments(this.key);

        for(String k: this.values.keySet())
            copy.values.put(k, this.values.get(k));

        return copy;
    }
}