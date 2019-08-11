package com.nilscoding.maven.mvndlwsdl;

import com.nilscoding.maven.mvndlwsdl.utils.DownloadUtils;
import com.nilscoding.maven.mvndlwsdl.utils.NamespaceResolver;
import com.nilscoding.maven.mvndlwsdl.utils.StringUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Maven plugin to download WSDL and XSD files to a folder, changing imports of
 * XSDs to relative path
 * @author NilsCoding
 */
@Mojo(name="dl-wsdl")
public class DownloadWsdlMojo extends AbstractMojo {

    @Parameter
    private String folder;
    
    @Parameter
    private String basename;
    
    @Parameter
    private String wsdlLocation;
    
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (StringUtils.isEmpty(this.folder) || StringUtils.isEmpty(this.basename) || StringUtils.isEmpty(this.wsdlLocation)) {
            return;
        }
        
        getLog().info("about to fetch WSDL from '" + this.wsdlLocation + "' to folder '" + this.folder + "'");
        
        // download WSDL file
        String wsdlContentStr = DownloadUtils.download(this.wsdlLocation, getLog());
        if (wsdlContentStr == null) {
            getLog().info("file not fetched from '" + this.wsdlLocation + "'");
            return;
        }
        
        try {
            List<Node> externalXsdNodes = new LinkedList<>();
            
            InputSource inputXml = new InputSource(new StringReader(wsdlContentStr));
            
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputXml);
            
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new NamespaceResolver(doc));
            
            // find all import nodes with schemaLocation attribute and store them in list
            XPathExpression xpathExpr = xpath.compile("//*[@schemaLocation]");
            NodeList list = (NodeList)xpathExpr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                externalXsdNodes.add(node);
            }
            
            if (externalXsdNodes.isEmpty() == false) {
                getLog().info("found " + externalXsdNodes.size() + " node(s) with external schema");
                
                // download each schema file
                for (int i = 0; i < externalXsdNodes.size(); i++) {
                    Node oneSchemaNode = externalXsdNodes.get(i);
                    String xsdLocation = oneSchemaNode.getAttributes().getNamedItem("schemaLocation").getTextContent();
                    String xsdContentStr = DownloadUtils.download(xsdLocation, getLog());
                    // generate output name
                    String outputName = this.basename + "_" + i + ".xsd";
                    // write schema file
                    String outputFullname = this.folder + outputName;
                    Path path = Paths.get(outputFullname);
                    path.toFile().getParentFile().mkdirs();
                    Files.write(path, Arrays.asList(xsdContentStr));
                    getLog().info("schema written to: " + outputFullname);
                    // replace schema file in node
                    oneSchemaNode.getAttributes().getNamedItem("schemaLocation").setTextContent(outputName);
                }
                
                // serialize xml document and write WSDL
                String wsdlOutputFullname = this.folder + this.basename + ".wsdl";
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                FileWriter writer = new FileWriter(new File(wsdlOutputFullname));
                StreamResult result = new StreamResult(writer);
                transformer.transform(source, result);
                getLog().info("written WSDL file: " + wsdlOutputFullname);
            }
            
        } catch (Exception ex) {
            getLog().error("error: " + ex);
        }
        
    }
    
    
}
