package bokarev.st.myapplication.models;

public class News {

    private String newsTitle, news;

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public News(String newsTitle, String news) {
        this.newsTitle = newsTitle;
        this.news = news;
    }
}
