package bokarev.st.myapplication.models;

public class Ads {

    private String adsTitle, ads;

    public Ads(String adsTitle, String ads) {
        this.adsTitle = adsTitle;
        this.ads = ads;
    }

    public String getAdsTitle() {
        return adsTitle;
    }

    public void setAdsTitle(String adsTitle) {
        this.adsTitle = adsTitle;
    }

    public String getAds() {
        return ads;
    }

    public void setAds(String ads) {
        this.ads = ads;
    }
}
