package RssReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class RssMain
{
    private Rss rss;

    public Rss getRss ()
    {
        return rss;
    }

    public void setRss (Rss rss)
    {
        this.rss = rss;
    }

    public void read(String URL_str){
        URLConnection url_con= null;
        try {
            url_con = new URL(URL_str).openConnection();
            url_con.connect();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url_con.getInputStream());
            rss=new Rss();

            Node nodeChannel = doc.getElementsByTagName("channel").item(0);
            Element elChannel = (Element) nodeChannel;
            Channel channel=new Channel();
            if(elChannel.getElementsByTagName("title").getLength() != 0) {
                channel.setTitle(elChannel.getElementsByTagName("title").item(0).getTextContent());
            }
            if(elChannel.getElementsByTagName("link").getLength() != 0) {
                channel.setLink(elChannel.getElementsByTagName("link").item(0).getTextContent());
            }
            if(elChannel.getElementsByTagName("description").getLength() != 0) {
                channel.setDescription(elChannel.getElementsByTagName("description").item(0).getTextContent().trim());
            }
            if(elChannel.getElementsByTagName("lastBuildDate").getLength() != 0) {
                channel.setLastBuildDate(elChannel.getElementsByTagName("lastBuildDate").item(0).getTextContent());
            }

            Element elImage = (Element) ((Element) nodeChannel).getElementsByTagName("image").item(0);
            Image image=new Image();

            if(elImage.getElementsByTagName("title").getLength()!= 0) {
                image.setTitle(elImage.getElementsByTagName("title").item(0).getTextContent());
            }
            if(elImage.getElementsByTagName("link").getLength() != 0) {
                image.setLink(elImage.getElementsByTagName("link").item(0).getTextContent());
            }
            if(elImage.getElementsByTagName("url").getLength() != 0) {
                image.setUrl(elImage.getElementsByTagName("url").item(0).getTextContent());
            }

            channel.setImage(image);
            NodeList nodeItems=elChannel.getElementsByTagName("item");
            Item[] item=new Item[nodeItems.getLength()];
            for(int i=0;i<item.length;i++){
                item[i]=new Item();
            }
            for(int i=0;i<nodeItems.getLength();i++){
                Node nNode = nodeItems.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element elItem = (Element) nodeItems.item(i);

                    if(elItem.getElementsByTagName("title").getLength() != 0) {
                        item[i].setTitle(elItem.getElementsByTagName("title").item(0).getTextContent());
                    }
                    if(elItem.getElementsByTagName("link").getLength() != 0) {
                        item[i].setLink(elItem.getElementsByTagName("link").item(0).getTextContent());
                    }
                    if(elItem.getElementsByTagName("description").getLength() != 0) {
                        item[i].setDescription(elItem.getElementsByTagName("description").item(0).
                                getTextContent().trim());
                    }
                    if(elItem.getElementsByTagName("guid").getLength() != 0) {
                        item[i].setGuid(elItem.getElementsByTagName("guid").item(0).getTextContent());
                    }
                    if(elItem.getElementsByTagName("pubDate").getLength() != 0) {
                        item[i].setPubDate(elItem.getElementsByTagName("pubDate").item(0).getTextContent());
                    }
                }
            }
            channel.setItem(item);
            rss.setChannel(channel);

            rss.setVersion(doc.getDocumentElement().getAttribute("version"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    } // Считывание новостей и перевод их в обьект

    public void writeToFile(){
        String path=new String("C:\\News\\" + rss.getChannel().getTitle().replace(" ","")
                .replace(":","\\"));
        new File(path).mkdirs();
        try (FileWriter writer =new FileWriter("\\"+path+"\\news_for_"+
                rss.getChannel().getItem()[0].getPubDate().replace(" ","_").replace(":","_").substring(0,20)+".txt")){

            StringBuilder text = new StringBuilder();
            for(int i=0;i<rss.getChannel().getItem().length;i++) {

                text.append("\t"+"Новость №"+(i+1));
                text.append("\r\n");
                text.append(rss.getChannel().getItem()[i].getTitle());
                text.append("\r\n");
                text.append(rss.getChannel().getItem()[i].getDescription());
                text.append("\r\n");
                text.append(rss.getChannel().getItem()[i].getLink());
                text.append("\r\n");
                text.append("\r\n");
            }

            writer.write(text.toString());
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }


    } // Запись новостей в текстовый файл

    public String[] textNews(){
        String[] text = new String[rss.getChannel().getItem().length];
        for(int i=0;i<rss.getChannel().getItem().length;i++) {

            text[i]=("\t"+"Новость №"+(i+1));
            text[i]+=("\r\n");
            text[i]+=(rss.getChannel().getItem()[i].getTitle());
            text[i]+=("\r\n");
            text[i]+=(rss.getChannel().getItem()[i].getDescription());
            text[i]+=("\r\n");
            text[i]+=(rss.getChannel().getItem()[i].getLink().replace("_","").replace("*",""));
            text[i]+=("\r\n");
            text[i]+=("\r\n");
        }
        return text;
    } //Вывод новостей в виде массива строк
    public void writeToRssFile() {

        try {
            String path = new String("C:\\News\\" + rss.getChannel().getTitle().replace(" ", "")
                    .replace(":", "\\"));

            new File(path).mkdirs();
            // create a XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory
                    .createXMLEventWriter(new FileOutputStream("\\" + path + "\\news_for_" +
                            rss.getChannel().getItem()[0].getPubDate().replace(" ", "_").replace(":", "_").substring(0, 20) + ".rss"));

            // create a EventFactory

            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");

            // create and write Start Tag

            StartDocument startDocument = eventFactory.createStartDocument();

            eventWriter.add(startDocument);

            // create open tag
            eventWriter.add(end);

            StartElement rssStart = eventFactory.createStartElement("", "", "rss");
            eventWriter.add(rssStart);
            eventWriter.add(eventFactory.createAttribute("version", "2.0"));
            eventWriter.add(end);

            eventWriter.add(eventFactory.createStartElement("", "", "channel"));
            eventWriter.add(end);

            createNode(eventWriter, "title", rss.getChannel().getTitle());

            createNode(eventWriter, "link", rss.getChannel().getLink());

            createNode(eventWriter, "description", rss.getChannel().getDescription());

            createNode(eventWriter, "lastBuidDate", rss.getChannel().getLastBuildDate());

            for (Item entry : rss.getChannel().getItem()) {
                eventWriter.add(eventFactory.createStartElement("", "", "item"));
                eventWriter.add(end);
                createNode(eventWriter, "title", entry.getTitle());
                createNode(eventWriter, "description", entry.getDescription());
                createNode(eventWriter, "link", entry.getLink());
                createNode(eventWriter, "guid", entry.getGuid());
                createNode(eventWriter, "pubDate", entry.getPubDate());
                eventWriter.add(end);
                eventWriter.add(eventFactory.createEndElement("", "", "item"));
                eventWriter.add(end);

            }
            eventWriter.add(eventFactory.createStartElement("", "", "image"));
            eventWriter.add(end);
            createNode(eventWriter, "title", rss.getChannel().getImage().getTitle());
            createNode(eventWriter, "link", rss.getChannel().getImage().getLink());
            createNode(eventWriter, "url", rss.getChannel().getImage().getUrl());
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndElement("", "", "image"));
            eventWriter.add(end);


            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndElement("", "", "channel"));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndElement("", "", "rss"));

            eventWriter.add(end);

            eventWriter.add(eventFactory.createEndDocument());

            eventWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    } //Запись новостей в файл формата .rss
    private void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }
    @Override
    public String toString()
    {
        return "ClassPojo [rss = "+rss+"]";
    }
}
			
		