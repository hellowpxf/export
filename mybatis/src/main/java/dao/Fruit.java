package dao;

/**
 * @description:Fruit
 * @author:pxf
 * @data:2022/09/30
 **/
public class Fruit {
    private String name;
    private  Double price;
    private String location;
    private String season;

    public Fruit() {
    }

    public Fruit(String name, Double price, String location, String season) {
        this.name = name;
        this.price = price;
        this.location = location;
        this.season = season;
    }

    @Override
    public String toString() {
        return "Fruit{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", season='" + season + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
