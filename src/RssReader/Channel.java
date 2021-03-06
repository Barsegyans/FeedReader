package RssReader;


public class Channel
{
    private String title;

    private String description;

    private String link;

    private String lastBuildDate;

    private Item[] item;

    private Image image;

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getLink ()
    {
        return link;
    }

    public void setLink (String link)
    {
        this.link = link;
    }

    public String getLastBuildDate ()
    {
        return lastBuildDate;
    }

    public void setLastBuildDate (String lastBuildDate)
    {
        this.lastBuildDate = lastBuildDate;
    }

    public Item[] getItem ()
    {
        return item;
    }

    public void setItem (Item[] item)
    {
        this.item = item;
    }

    public Image getImage ()
    {
        return image;
    }

    public void setImage (Image image)
    {
        this.image = image;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [title = "+title+", description = "+description+", link = "+link+", lastBuildDate = "+lastBuildDate+", item = "+item+", image = "+image+"]";
    }
}