package com.nilscoding.maven.mvndlwsdl.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * XML utils.
 * @author NilsCoding
 */
public final class XmlUtils {

    private XmlUtils() {
    }

    /**
     * Parses the given string to XML document.
     * @param xmlStr XML string to parse
     * @return parsed document or null on error
     */
    public static Document loadXmlFromString(String xmlStr) {
        if (StringUtils.isEmpty(xmlStr)) {
            return null;
        }
        try {
            InputSource inputXml = new InputSource(new StringReader(xmlStr));
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputXml);
            return doc;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Writes the document to a file.
     * @param xmlDoc   XML document to write
     * @param filename filename
     * @return true on success, false on error
     */
    public static boolean writeXmlToFile(Document xmlDoc, String filename) {
        if ((xmlDoc == null) || (StringUtils.isEmpty(filename))) {
            return false;
        }
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(xmlDoc);
            FileWriter writer = new FileWriter(filename);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Find all nodes in an XML document.
     * @param xmlDoc   XML document
     * @param attrName attribute name
     * @return list with all nodes, can be empty
     */
    public static List<Node> findNodesWithAttribute(Document xmlDoc, String attrName) {
        List<Node> nodeList = new LinkedList<>();
        if ((xmlDoc == null) || StringUtils.isEmpty(attrName)) {
            return nodeList;
        }
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new AdaptiveNamespaceResolver(xmlDoc));

            // find all import nodes with schemaLocation attribute and store them in list
            XPathExpression xpathExpr = xpath.compile("//*[@" + attrName + "]");
            NodeList list = (NodeList) xpathExpr.evaluate(xmlDoc, XPathConstants.NODESET);
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                nodeList.add(node);
            }
        } catch (Exception ex) {
        }
        return nodeList;
    }

    /**
     * Returns the attribute by name from a Node.
     * @param node     Node
     * @param attrName attribute name
     * @return attribute text or null if not found
     */
    public static String getAttributeTextByName(Node node, String attrName) {
        if ((node == null) || (StringUtils.isEmpty(attrName))) {
            return null;
        }
        NamedNodeMap nnm = node.getAttributes();
        if ((nnm == null) || (nnm.getLength() == 0)) {
            return null;
        }
        Node attrNode = nnm.getNamedItem(attrName);
        if (attrNode == null) {
            return null;
        }
        try {
            return attrNode.getTextContent();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Sets the attribute name to given value.
     * @param node      Node
     * @param attrName  attribute name
     * @param attrValue attribute value
     * @return true on success, false on error
     */
    public static boolean setAttributeTextByName(Node node, String attrName, String attrValue) {
        if ((node == null) || (StringUtils.isEmpty(attrName))) {
            return false;
        }
        NamedNodeMap nnm = node.getAttributes();
        Node attrNode = nnm.getNamedItem(attrName);
        if (attrNode != null) {
            if (attrValue == null) {
                nnm.removeNamedItem(attrName);
            } else {
                attrNode.setTextContent(attrValue);
            }
        } else if (attrValue != null) {
            Attr newAttrNode = node.getOwnerDocument().createAttribute(attrName);
            newAttrNode.setValue(attrValue);
            nnm.setNamedItem(newAttrNode);
        }
        return true;
    }

}
