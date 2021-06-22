package sber.itschool.WeatherBot.Config;

public class User {

    String city;
    Integer zipCode;
    String countryCode;
    Float latitude;
    Float longitude;
    BotState botState;

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

//    public Integer getZipCode() {
//        return zipCode;
//    }
//
//    public void setZipCode(Integer zipCode) {
//        this.zipCode = zipCode;
//    }
//
//    public String getCountryCode() {
//        return countryCode;
//    }
//
//    public void setCountryCode(String countryCode) {
//        this.countryCode = countryCode;
//    }
//
//    public Float getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(Float latitude) {
//        this.latitude = latitude;
//    }
//
//    public Float getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(Float longitude) {
//        this.longitude = longitude;
//    }
}
