package com.nilscoding.maven.mvndlwsdl.utils;

import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Document;

/**
 * adaptive Namespace Resolver, taken from: https://howtodoinjava.com/xml/xpath-namespace-resolution-example/
 * @author Lokesh Gupta
 */
public class NamespaceResolver implements NamespaceContext
{
    private final Document sourceDocument;
 
    public NamespaceResolver(Document document) {
        sourceDocument = document;
    }
 
    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return sourceDocument.lookupNamespaceURI(null);
        } else {
            return sourceDocument.lookupNamespaceURI(prefix);
        }
    }
 
    @Override
    public String getPrefix(String namespaceURI) {
        return sourceDocument.lookupPrefix(namespaceURI);
    }
 
    @SuppressWarnings("rawtypes")
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
    
}