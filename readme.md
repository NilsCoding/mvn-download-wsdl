# mvn-download-wsdl

This is a Maven Pluging to download a WSDL file and all ist XSD files, storing them together in one directory and change the Imports in the WSDL file accordingly.

# Configuration

## Example

```xml
<plugin>
    <groupId>com.nilscoding.maven</groupId>
    <artifactId>mvn-download-wsdl</artifactId>
    <version>1.0.0</version>
    <configuration>
        <wsdlLocation>https://yourserver/someservice/someservice?wsdl</wsdlLocation>
        <folder>${basedir}/src/main/resources/META-INF/wsdl/</folder>
        <basename>someservice</basename>
    </configuration>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>dl-wsdl</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Details

The configuration takes three parameters:
1. wsdlLocation: the HTTP/HTTPS location of the WSDL file
2. folder: the target directory to write all downloaded files to
3. basename: the base name for the output files

### wsdlLocation

This parameter references the URL location of the WSDL file to download. It must be a HTTP or HTTPS url, other protocols (e.g. local file) are currently not supported.

Internally okhttp3 is used for downloading. It is configured to ignore SSL certificate errors, so on the one hand you can use locations with self-signed certificates, but on the our hand any certificate is accepted, even "evil" ones.

As of version 1.0.0 of this plugin, no proxy servers are supported, so the WSDL must be reachable directly. Also, the location must not be protected (e.g. no basic auth).

### folder

The output folder to write all files to.

When using the `jaxws-maven-plugin` with `wsimport` you might want to use the folder configuration from the example above and this `wsimport` configuration snippet:

```xml
<configuration>
    <wsdlDirectory>src/main/resources/META-INF/wsdl</wsdlDirectory>
    <packageName>your.custom.package</packageName>
    <vmArgs>
        <vmArg>-Djavax.xml.accessExternalSchema=all</vmArg>
    </vmArgs>
    <wsdlLocation>META-INF/wsdl/someservice.wsdl</wsdlLocation>
    <staleFile>${project.build.directory}/jaxws/stale/someservice.stale</staleFile>
</configuration>
```

### basename

The base name for the output files.

Taking the example above, this files will be written, assuming that the WSDL file contains two schema imports:
* someservice.wsdl
* someservice_0.xsd
* someservice_1.xsd

The schema files will be named with a counting number, beginning at 0. The original schema file name from the WSDL file will be ignored and replaced by this name.

# Usage

This plugin is not hosted on any official Maven repository. The recommended way to use it is to clone this Github repository and install the plugin locally (with `mvn install`). This makes it available to other build processes.

If you have an internal Maven repository server like Nexus, you may upload it there.

# Copyright / License

mvn-download-wsdl is licensed under the MIT License

## The MIT License (MIT)

Copyright (c) 2019 NilsCoding

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
