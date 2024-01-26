package com.nilscoding.maven.mvndlwsdl;

import com.nilscoding.maven.mvndlwsdl.utils.DownloadUtils;
import com.nilscoding.maven.mvndlwsdl.utils.SchemaFile;
import com.nilscoding.maven.mvndlwsdl.utils.StringUtils;
import com.nilscoding.maven.mvndlwsdl.utils.XmlUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Maven plugin to download WSDL and XSD files to a folder,
 * changing imports of XSDs to relative path.
 * @author NilsCoding
 */
@Mojo(name = "dl-wsdl")
public class DownloadWsdlMojo extends AbstractMojo {

    /**
     * Parameter: folder.
     */
    @Parameter
    private String folder;

    /**
     * Parameter: basename.
     */
    @Parameter
    private String basename;

    /**
     * Parameter: wsdlLocation.
     */
    @Parameter
    private String wsdlLocation;

    /**
     * Parameter: downloaderClass.
     */
    @Parameter
    private String downloaderClass;

    /**
     * Parameter: downloaderOptions.
     */
    @Parameter
    private String downloaderOptions;

    /**
     * Executes the Maven Mojo.
     * @throws MojoExecutionException Mojo execution exception
     * @throws MojoFailureException   Mojo failure exception
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (StringUtils.isEmpty(this.folder)
                || StringUtils.isEmpty(this.basename)
                || StringUtils.isEmpty(this.wsdlLocation)) {
            return;
        }

        getLog().info("about to fetch WSDL from '" + this.wsdlLocation + "' to folder '" + this.folder + "'");

        // download WSDL file
        String wsdlContentStr = DownloadUtils.download(this.wsdlLocation,
                getLog(),
                this.downloaderClass,
                this.downloaderOptions);
        if (wsdlContentStr == null) {
            getLog().info("file not fetched from '" + this.wsdlLocation + "'");
            return;
        }
        getLog().info("wsdl length: " + wsdlContentStr.length());

        try {
            Document wsdlDocument = XmlUtils.loadXmlFromString(wsdlContentStr);
            List<Node> externalXsdNodes = XmlUtils.findNodesWithAttribute(wsdlDocument, "schemaLocation");

            Map<String, SchemaFile> resolvedSchemas = new LinkedHashMap<>();

            getLog().info("found " + externalXsdNodes.size() + " node(s) with external schema in WSDL");
            if (externalXsdNodes.isEmpty() == false) {

                // download files from WSDL
                for (int i = 0; i < externalXsdNodes.size(); i++) {
                    Node oneSchemaNode = externalXsdNodes.get(i);
                    String xsdLocation = XmlUtils.getAttributeTextByName(oneSchemaNode, "schemaLocation");
                    String targetNamespace = XmlUtils.getAttributeTextByName(oneSchemaNode, "namespace");
                    String xsdContentStr = DownloadUtils.download(xsdLocation,
                            getLog(),
                            this.downloaderClass,
                            this.downloaderOptions);
                    if (resolvedSchemas.containsKey(targetNamespace) == false) {
                        SchemaFile schemaFile = new SchemaFile();
                        schemaFile.setSourceUrl(xsdLocation);
                        schemaFile.setNamespace(targetNamespace);
                        if (schemaFile.parseXml(xsdContentStr)) {
                            resolvedSchemas.put(targetNamespace, schemaFile);
                        }
                    }
                }

                // check each schema if it contains other schemas
                boolean atLeastOneSchemaWasAdded = false;
                do {
                    List<SchemaFile> tmpSchemaFiles = new LinkedList<>();
                    for (SchemaFile oneFile : resolvedSchemas.values()) {
                        if (oneFile.isProcessed()) {
                            continue;
                        }
                        Document tmpDocument = oneFile.getDocument();
                        if (tmpDocument != null) {
                            List<Node> foundSchemaNodes = XmlUtils.findNodesWithAttribute(tmpDocument,
                                    "schemaLocation");
                            if ((foundSchemaNodes != null) && (foundSchemaNodes.isEmpty() == false)) {
                                // check if node references a namespace that is not known yet
                                for (Node oneSchemaNode : foundSchemaNodes) {
                                    String targetNamespace = XmlUtils.getAttributeTextByName(oneSchemaNode,
                                            "namespace");
                                    if (StringUtils.isEmpty(targetNamespace) == false) {
                                        if (resolvedSchemas.containsKey(targetNamespace) == false) {
                                            // download schema file
                                            String xsdLocation = XmlUtils.getAttributeTextByName(oneSchemaNode,
                                                    "schemaLocation");
                                            String xsdContentStr = DownloadUtils.download(xsdLocation,
                                                    getLog(),
                                                    this.downloaderClass,
                                                    this.downloaderOptions);
                                            if (StringUtils.isEmpty(xsdContentStr) == false) {
                                                SchemaFile schemaFile = new SchemaFile();
                                                schemaFile.setSourceUrl(xsdLocation);
                                                schemaFile.setNamespace(targetNamespace);
                                                if (schemaFile.parseXml(xsdContentStr)) {
                                                    resolvedSchemas.put(targetNamespace, schemaFile);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        oneFile.setProcessed(true);
                    }
                    if (tmpSchemaFiles.isEmpty() == false) {
                        for (SchemaFile oneFile : tmpSchemaFiles) {
                            if (resolvedSchemas.containsKey(oneFile.getNamespace()) == false) {
                                resolvedSchemas.put(oneFile.getNamespace(), oneFile);
                                atLeastOneSchemaWasAdded = true;
                            }
                        }
                    }
                } while (atLeastOneSchemaWasAdded == true);

                // every schema should have been found by now

                // let's assign a unique local name to each schema
                int xsdIndex = 0;
                int xsdCount = resolvedSchemas.size();
                for (SchemaFile oneFile : resolvedSchemas.values()) {
                    String tmpName = this.basename + "_" + StringUtils.formatLeadingZeros(xsdIndex, xsdCount) + ".xsd";
                    oneFile.setTemporaryName(tmpName);
                    xsdIndex++;
                }

                // scan each schema file again and replace the schema location with the temporary name,
                //   based on the namespace
                for (SchemaFile oneFile : resolvedSchemas.values()) {
                    Document oneDocument = oneFile.getDocument();
                    if (oneDocument == null) {
                        continue;
                    }
                    List<Node> schemaLocationNodes = XmlUtils.findNodesWithAttribute(oneDocument,
                            "schemaLocation");
                    if ((schemaLocationNodes != null) && (schemaLocationNodes.isEmpty() == false)) {
                        for (Node oneNode : schemaLocationNodes) {
                            String namespace = XmlUtils.getAttributeTextByName(oneNode, "namespace");
                            if (StringUtils.isEmpty(namespace) == false) {
                                SchemaFile referencedFile = resolvedSchemas.get(namespace);
                                if (referencedFile != null) {
                                    String tmpSchemaLocation = referencedFile.getTemporaryName();
                                    XmlUtils.setAttributeTextByName(oneNode, "schemaLocation",
                                            tmpSchemaLocation);
                                } else {
                                    getLog().warn("no schema location changed for '" + namespace + "'");
                                }
                            }
                        }
                    }
                }

                // also process the WSDL file
                for (Node wsdlXsdNodes : externalXsdNodes) {
                    String namespace = XmlUtils.getAttributeTextByName(wsdlXsdNodes, "namespace");
                    if (StringUtils.isEmpty(namespace) == false) {
                        SchemaFile referencedFile = resolvedSchemas.get(namespace);
                        if (referencedFile != null) {
                            String tmpSchemaLocation = referencedFile.getTemporaryName();
                            XmlUtils.setAttributeTextByName(wsdlXsdNodes, "schemaLocation", tmpSchemaLocation);
                        }
                    }
                }
            }

            // write WSDL file
            String wsdlOutputFullname = this.folder + this.basename + ".wsdl";
            XmlUtils.writeXmlToFile(wsdlDocument, wsdlOutputFullname);
            getLog().info("written WSDL file: " + wsdlOutputFullname);

            // write schema files
            if (resolvedSchemas.isEmpty() == false) {
                for (SchemaFile oneSchemaFile : resolvedSchemas.values()) {
                    if ((oneSchemaFile != null) && (oneSchemaFile.getDocument() != null)) {
                        String oneFilename = oneSchemaFile.getTemporaryName();
                        if (StringUtils.isEmpty(oneFilename) == false) {
                            String outputFullname = this.folder + oneFilename;
                            Path path = Paths.get(outputFullname);
                            path.toFile().getParentFile().mkdirs();
                            if (XmlUtils.writeXmlToFile(oneSchemaFile.getDocument(), outputFullname)) {
                                getLog().info("schema written to: " + outputFullname);
                            } else {
                                getLog().error("could not write schema to: " + outputFullname);
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            getLog().error("error: " + ex);
        }

    }

}
