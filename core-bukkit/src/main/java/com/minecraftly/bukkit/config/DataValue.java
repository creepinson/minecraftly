package com.minecraftly.bukkit.config;

import static com.google.common.base.Preconditions.checkNotNull;

import com.minecraftly.bukkit.utilities.BukkitUtilities;

/**
 * Represents a data value of which we can track the content owner and it's original value.
 */
public class DataValue<T> {

    private T def;
    private T value;
    private T untouchedValue;
    private Class<T> typeClass;

    private Object handler = null;

    public DataValue(T def, Class<T> typeClass) {
        checkNotNull(def);

        this.def = def;
        this.typeClass = typeClass;
        setValue(def);
    }

    /**
     * Gets if the value has been changed from the default value.
     *
     * @return is value changed
     */
    public boolean isValueDefault() {
        return getValue().equals(getDefaultValue());
    }

    public T getDefaultValue() {
        return def;
    }

    public T getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    public void setValue(T value) {
        if (value == null) {
            this.untouchedValue = getDefaultValue();
        } else {
            this.untouchedValue = value;
        }

        if (this.untouchedValue instanceof String) {
            this.value = (T) BukkitUtilities.translateAlternateColorCodes('&', (String) this.untouchedValue); // this is legal
        } else {
            this.value = value;
        }
    }

    public T getUntouchedValue() {
        return untouchedValue;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    // very internal methods

    protected Object getHandler() {
        return handler;
    }

    protected void setHandler(Object handler) {
        this.handler = handler;
    }
}
