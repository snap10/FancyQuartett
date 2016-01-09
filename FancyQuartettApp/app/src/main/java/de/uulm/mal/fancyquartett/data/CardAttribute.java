package de.uulm.mal.fancyquartett.data;

import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;

/**
 * Created by Lukas on 09.01.2016.
 */
public class CardAttribute {

    private Property property;
    private float value;

    public CardAttribute(Property property, float value) {
        this.property = property;
        this.value = value;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
