package com.nilscoding.maven.mvndlwsdl.utils;

import org.w3c.dom.Document;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * Adaptive Namespace Resolver. Taken from: https://howtodoinjava.com/xml/xpath-namespace-resolution-example/
 * @author Lokesh Gupta
 */
public final class AdaptiveNamespaceResolver implements NamespaceContext {
    /**
     * Source document.
     */
    private final Document sourceDocument;

    /**
     * Creates a new instance with given document.
     * @param document document
     */
    public AdaptiveNamespaceResolver(Document document) {
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
    public Iterator<String> getPrefixes(String namespaceURI) {
        return null;
    }

}
