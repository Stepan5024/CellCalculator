package bokarev.st.myapplication.models;

public class Ads {

    private String typeOfWorkTitle, ads;

    public Ads(String typeOfWorkTitle, String ads) {
        this.typeOfWorkTitle = typeOfWorkTitle;
        this.ads = ads;
    }

    public String getTypeOfWorkTitle() {
        return typeOfWorkTitle;
    }

    public void setTypeOfWorkTitle(String adsTitle) {
        this.typeOfWorkTitle = typeOfWorkTitle;
    }

    public String getAds() {
        return ads;
    }

    public void setAds(String ads) {
        this.ads = ads;
    }
}
