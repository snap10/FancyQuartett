package de.uulm.mal.fancyquartett.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Lukas on 09.01.2016.
 */
public class CardAttribute implements Serializable{

    private Property property;
    private double value;
    private double median;
    private int id;
    private int cardID;
    private String attributeName;
    private String unit;
    private int order;
    private Image attributeImage;
    private boolean higherWins;


    public CardAttribute(Property property, float value) {
        this.property = property;
        this.value = value;
    }

    public CardAttribute(JSONObject attrJson) throws JSONException {
            this.id=attrJson.getInt("id");
            this.cardID=attrJson.getInt("card");
        this.order=attrJson.getInt("order");
        this.attributeName=attrJson.getString("name");
        this.attributeImage=new Image(attrJson.getString("image"));
        this.unit=attrJson.getString("unit");
        if (attrJson.getString("what_wins").equals("higher_wins")){
            this.higherWins=true;
        }else{
            this.higherWins=false;
        }
        this.value=attrJson.getDouble("value");
        this.median=attrJson.getDouble("median");
        property = new Property(higherWins,attributeImage,order,unit,attributeName,cardID,id,median);
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public double getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Image getAttributeImage() {
        return attributeImage;
    }

    public void setAttributeImage(Image attributeImage) {
        this.attributeImage = attributeImage;
    }

    public boolean isHigherWins() {
        return higherWins;
    }

    public void setHigherWins(boolean higherWins) {
        this.higherWins = higherWins;
    }
}
