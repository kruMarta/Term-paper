package candy;

public class Candy {

    private String name;
    private String weight;
    private String price;
    private String sugarContent;
    private String wrapperColor;
    private String consumerRating;
    private String caloricContent;

    public Candy() {

    }

    public Candy(String name, String weight, String price, String consumerRating, String caloricContent,
                 String sugarContent, String wrapperColor) {
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.sugarContent = sugarContent;
        this.wrapperColor = wrapperColor;
        this.consumerRating = consumerRating;
        this.caloricContent = caloricContent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setSugarContent(String sugarContent) {
        this.sugarContent = sugarContent;
    }

    public String getSugarContent() {
        return sugarContent;
    }

    public void setWrapperColor(String wrapperColor) {
        this.wrapperColor = wrapperColor;
    }

    public void setConsumerRating(String consumerRating) {
        this.consumerRating = consumerRating;
    }

    public String getConsumerRating() {
        return consumerRating;
    }

    public void setCaloricContent(String caloricContent) {
        this.caloricContent = caloricContent;
    }

    public String getCaloricContent() {
        return caloricContent;
    }

    public String getWrapperColor() {
        return wrapperColor;
    }

    @Override
    public String toString() {
        return "Candy: " + name + ", "  + weight + "g, " + price + " UAN, " + sugarContent + "g of sugar, "+
                wrapperColor.toLowerCase() + " wrapping, " + consumerRating + "th position in the consumer rating, " +
                 caloricContent + "kcal.";
    }
}
