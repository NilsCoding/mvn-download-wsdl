package com.nilscoding.maven.mvndlwsdl.utils;

import org.w3c.dom.Document;
import java.io.Serializable;

/**
 * Schema file.
 * @author NilsCoding
 */
public class SchemaFile implements Serializable {
    private static final long serialVersionUID = -633597038211543229L;

    /**
     * Namespace.
     */
    protected String namespace;
    /**
     * Source URL.
     */
    protected String sourceUrl;
    /**
     * XML document.
     */
    protected Document document;
    /**
     * Processed flag.
     */
    protected boolean processed;
    /**
     * Temporary name.
     */
    protected String temporaryName;

    /**
     * Creates a new instance.
     */
    public SchemaFile() {
    }

    /**
     * Parses the XML document.
     * @param fileDataStr file data to set
     * @return true if string was parsed to XML document, false otherwise
     */
    public boolean parseXml(String fileDataStr) {
        if (fileDataStr == null) {
            this.document = null;
        } else {
            this.document = XmlUtils.loadXmlFromString(fileDataStr);
        }
        return (this.document != null);
    }

    /**
     * Returns the namespace.
     * @return namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace.
     * @param namespace namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Returns the source URL.
     * @return source URL
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * Sets the source URL.
     * @param sourceUrl source URL to set
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     * Returns the XML document.
     * @return XML document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the XML document.
     * @param document XML document to set
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Returns the processed flag.
     * @return processed flag
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Sets the processed flag.
     * @param processed processed flag to set
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * Returns the temporary name.
     * @return temporary name
     */
    public String getTemporaryName() {
        return temporaryName;
    }

    /**
     * Sets the temporary name.
     * @param temporaryName temporary name to set
     */
    public void setTemporaryName(String temporaryName) {
        this.temporaryName = temporaryName;
    }
}
