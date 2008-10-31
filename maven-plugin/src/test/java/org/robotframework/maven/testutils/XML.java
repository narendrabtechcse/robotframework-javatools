package org.robotframework.maven.testutils;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XML {

    public static Document parse(File xml) {
        try {
            return DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(xml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void serialize(Document xml, File to)
            throws TransformerFactoryConfigurationError {
        try {
            TransformerFactory.newInstance().newTransformer()
                    .transform(new DOMSource(xml),
                            new StreamResult(to));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
