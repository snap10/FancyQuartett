package de.uulm.mal.fancyquartett.data;

import java.io.Serializable;

/**
 * Created by mk on 01.01.2016.
 */
public class Deck implements Serializable {


    protected int id;
    protected String name = null;
    protected String description = null;
    protected Image deckimage;
    protected String misc;
    protected String misc_version;
    public Deck(){};
    public Deck(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Deck(int id, String name, String description, Image deckimage, String misc, String misc_version) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deckimage = deckimage;
        this.misc = misc;
        this.misc_version = misc_version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public int getId() {
        return id;
    }

    public Image getDeckimage() {
        return deckimage;
    }

    public String getMisc() {
        return misc;
    }

    public String getMisc_version() {
        return misc_version;
    }

    /**
     * Compares this instance with the specified object and indicates if they
     * are equal. In order to be equal, {@code o} must represent the same object
     * as this instance using a class-specific comparison. The general contract
     * is that this comparison should be reflexive, symmetric, and transitive.
     * Also, no object reference other than null is equal to null.
     * <p/>
     * <p>The default implementation returns {@code true} only if {@code this ==
     * o}. See <a href="{@docRoot}reference/java/lang/Object.html#writing_equals">Writing a correct
     * {@code equals} method</a>
     * if you intend implementing your own {@code equals} method.
     * <p/>
     * <p>The general contract for the {@code equals} and {@link
     * #hashCode()} methods is that if {@code equals} returns {@code true} for
     * any two objects, then {@code hashCode()} must return the same value for
     * these objects. This means that subclasses of {@code Object} usually
     * override either both methods or neither of them.
     *
     * @param other the object to compare this instance with.
     * @return {@code true} if the specified object is equal to this {@code
     * Object}; {@code false} otherwise.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Deck))return false;
        Deck otherDeck = (Deck)other;
        return (this.id==otherDeck.id);

    }


}
