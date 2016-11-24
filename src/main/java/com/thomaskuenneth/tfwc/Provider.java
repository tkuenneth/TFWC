/*
 * Provider.java - This file is part of TFWC
 * Copyright (C) 2016  Thomas Kuenneth
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thomaskuenneth.tfwc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class provides the comics through parsing an atom feed.
 *
 * @author Thomas Kuenneth
 */
public class Provider {

    private static final Logger LOGGER = Logger.getLogger(Provider.class.getName());
    private static final String ATOM_URL = "http://www.xkcd.com/atom.xml";
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Gets the list of recent comics sorted by date in reverse order (newest
     * comes first). The returned list is never null, but may have zero elements
     * if there was an error while retrieving the feed and its related
     * resources.
     *
     * @return list of recent comics
     */
    public List<Comic> getSortedListOfComics() {
        List<Comic> result = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(ATOM_URL);
            NodeList nodes = doc.getElementsByTagName("entry");
            for (int i = 0; i < nodes.getLength(); i++) {
                Comic current = new Comic();
                result.add(current);
                Node node = nodes.item(i);
                NodeList children = node.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    switch (child.getNodeName()) {
                        case "title":
                            current.title = child.getFirstChild().getNodeValue();
                            break;
                        case "updated":
                            String strDate = child.getFirstChild().getNodeValue();
                            try {
                                Date updated = DF.parse(strDate);
                                current.updated = updated;
                            } catch (ParseException ex) {
                                LOGGER.log(Level.SEVERE, "failed to parse " + strDate, ex);
                            }
                            break;
                        case "summary":
                            parseSummary(child.getFirstChild().getNodeValue(),
                                    current);
                            break;
                    }
                }
            }
            Collections.sort(result, Collections.reverseOrder((Comic o1, Comic o2) -> {
                Date d1 = o1.updated;
                Date d2 = o2.updated;
                if (d1.before(d2)) {
                    return -1;
                } else if (d1.after(d2)) {
                    return 1;
                } else {
                    return 0;
                }
            }));
        } catch (IllegalArgumentException | IOException | SAXException |
                ParserConfigurationException ex) {
            LOGGER.log(Level.SEVERE, "failed to load atom data", ex);
        }
        return result;
    }

    private void parseSummary(String summary, Comic comic) {
        ByteArrayInputStream in = new ByteArrayInputStream(summary.getBytes(Charset.forName("UTF-8")));
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            Node imgTag = doc.getFirstChild();
            NamedNodeMap atts = imgTag.getAttributes();
            comic.summary = atts.getNamedItem("title").getNodeValue();
            comic.url = atts.getNamedItem("src").getNodeValue();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOGGER.log(Level.SEVERE, "parseSummary()", ex);
        }
    }
}
