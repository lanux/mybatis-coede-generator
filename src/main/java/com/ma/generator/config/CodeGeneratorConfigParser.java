package com.ma.generator.config;

import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ParserEntityResolver;
import org.mybatis.generator.config.xml.ParserErrorHandler;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by lanux on 2016/4/22.
 */
public class CodeGeneratorConfigParser {

    private Properties properties = new Properties();

    public CodeGeneratorConfigParser() {
    }

    public CodeGenConfiguration parseConfiguration(File inputFile) throws IOException,
            XMLParserException {

        FileReader fr = new FileReader(inputFile);

        return this.parseConfiguration(fr);
    }

    public CodeGenConfiguration parseConfiguration(Reader reader) throws IOException,
            XMLParserException {

        InputSource is = new InputSource(reader);

        return parseConfiguration(is);
    }

    public CodeGenConfiguration parseConfiguration(InputStream inputStream)
            throws IOException, XMLParserException {

        InputSource is = new InputSource(inputStream);

        return parseConfiguration(is);
    }

    private CodeGenConfiguration parseConfiguration(InputSource inputSource) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new ParserEntityResolver());

            ParserErrorHandler handler = new ParserErrorHandler(new ArrayList<>(), new ArrayList<>());
            builder.setErrorHandler(handler);

            Document document = builder.parse(inputSource);

            Element rootNode = document.getDocumentElement();
            CodeGenConfiguration configuration = new CodeGenConfiguration();
            NodeList nodeList = rootNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);

                if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                if ("properties".equals(childNode.getNodeName())) { //$NON-NLS-1$
                    parseProperties(configuration, childNode);
                } else if ("classPathEntry".equals(childNode.getNodeName())) { //$NON-NLS-1$
                    parseClassPathEntry(configuration, childNode);
                } else if ("jdbcConnection".equals(childNode.getNodeName())) { //$NON-NLS-1$
                    parseJdbcConnection(configuration, childNode);
                } else if ("project".equals(childNode.getNodeName())) { //$NON-NLS-1$
                    parseProjectInfo(configuration, childNode);
                } else if ("table".equals(childNode.getNodeName())) { //$NON-NLS-1$
                    parseTable(configuration, childNode);
                }
            }
            return configuration;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseProjectInfo(CodeGenConfiguration context, Node node) {
        Properties attributes = parseAttributes(node);

        String targetPackage = attributes.getProperty("targetPackage"); //$NON-NLS-1$
        String targetProject = attributes.getProperty("targetProject"); //$NON-NLS-1$
        if (stringHasValue(targetPackage)) {
            context.setTargetPackage(targetPackage);
        }
        if (stringHasValue(targetProject)) {
            context.setTargetProject(targetProject);
        }
    }

    private void parseProperties(CodeGenConfiguration configuration, Node node)
            throws XMLParserException {
        Properties attributes = parseAttributes(node);
        String resource = attributes.getProperty("resource"); //$NON-NLS-1$
        String url = attributes.getProperty("url"); //$NON-NLS-1$

        if (!stringHasValue(resource)
                && !stringHasValue(url)) {
            throw new XMLParserException(getString("RuntimeError.14")); //$NON-NLS-1$
        }

        URL resourceUrl;

        try {
            InputStream inputStream = null;
            if (stringHasValue(resource)) {
                resourceUrl = ObjectFactory.getResource(resource);
                if (resourceUrl == null) {
                    if (url!=null){
                        inputStream = new FileInputStream(new File(url));
                    }
                    if (inputStream==null){
                        throw new XMLParserException(getString(
                                "RuntimeError.15", resource)); //$NON-NLS-1$
                    }
                }else{
                    inputStream = resourceUrl.openConnection()
                            .getInputStream();
                }
            } else {
                inputStream = new FileInputStream(new File(url));
            }

            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            if (stringHasValue(resource)) {
                throw new XMLParserException(getString(
                        "RuntimeError.16", resource)); //$NON-NLS-1$
            } else {
                throw new XMLParserException(getString(
                        "RuntimeError.17", url)); //$NON-NLS-1$
            }
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                Properties properties1 = parseAttributes(childNode);

                String name = properties1.getProperty("name"); //$NON-NLS-1$
                String value = properties1.getProperty("value"); //$NON-NLS-1$

                properties.put(name, value);
            }
        }

        configuration.setProperties(properties);

    }

    private void parseTable(CodeGenConfiguration context, Node node) {
        TableConfig tc = new TableConfig();
        Properties attributes = parseAttributes(node);
        String enable = attributes.getProperty("enable"); //$NON-NLS-1$
        if (stringHasValue(enable) && !isTrue(enable)) {
            return;
        }
        context.addTableConfiguration(tc);
        String catalog = attributes.getProperty("catalog"); //$NON-NLS-1$
        String module = attributes.getProperty("module"); //$NON-NLS-1$
        String schema = attributes.getProperty("schema"); //$NON-NLS-1$
        String tableName = attributes.getProperty("tableName"); //$NON-NLS-1$
        String domainObjectName = attributes.getProperty("domainObjectName"); //$NON-NLS-1$
        if (stringHasValue(catalog)) {
            tc.setCatalog(catalog.trim());
        }
        if (stringHasValue(module)) {
            tc.setModule(module.trim());
        }

        if (stringHasValue(schema)) {
            tc.setSchema(schema.trim());
        }

        if (stringHasValue(tableName)) {
            tc.setTableName(tableName.trim());
        }

        if (stringHasValue(domainObjectName)) {
            tc.setDomainObjectName(domainObjectName.trim());
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(tc, childNode);
            } else if ("exclude".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseExcludeColumn(tc, childNode);
            } else if ("include".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseIncludeColumn(tc, childNode);
            }
        }
    }

    private void parseExcludeColumn(TableConfig tc, Node node) {
        String excludes = node.getTextContent();
        if (!stringHasValue(excludes)) {
            Properties attributes = parseAttributes(node);
            excludes = attributes.getProperty("value"); //$NON-NLS-1$
        }
        String[] strings = excludes.split(",");
        HashSet<String> exclude = new HashSet<>();
        tc.setExcludes(exclude);
        for (String columnName : strings) {
            exclude.add(columnName);
        }
    }

    private void parseIncludeColumn(TableConfig tc, Node node) {
        String includes = node.getTextContent();
        if (!stringHasValue(includes)) {
            Properties attributes = parseAttributes(node);
            includes = attributes.getProperty("value"); //$NON-NLS-1$
        }
        String[] strings = includes.split(",");
        HashSet<String> include = new HashSet<>();
        tc.setIncludes(include);
        for (String columnName : strings) {
            include.add(columnName);
        }
    }

    private void parseGeneratedKey(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);

        String column = attributes.getProperty("column"); //$NON-NLS-1$
        boolean identity = isTrue(attributes
                .getProperty("identity")); //$NON-NLS-1$
        String sqlStatement = attributes.getProperty("sqlStatement"); //$NON-NLS-1$
        String type = attributes.getProperty("type"); //$NON-NLS-1$

        GeneratedKey gk = new GeneratedKey(column, sqlStatement, identity, type);

        tc.setGeneratedKey(gk);
    }

    private void parseIgnoreColumn(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String column = attributes.getProperty("column"); //$NON-NLS-1$
        String delimitedColumnName = attributes
                .getProperty("delimitedColumnName"); //$NON-NLS-1$

        IgnoredColumn ic = new IgnoredColumn(column);

        if (stringHasValue(delimitedColumnName)) {
            ic.setColumnNameDelimited(isTrue(delimitedColumnName));
        }

        tc.addIgnoredColumn(ic);
    }

    private void parseColumnRenamingRule(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String searchString = attributes.getProperty("searchString"); //$NON-NLS-1$
        String replaceString = attributes.getProperty("replaceString"); //$NON-NLS-1$

        ColumnRenamingRule crr = new ColumnRenamingRule();

        crr.setSearchString(searchString);

        if (stringHasValue(replaceString)) {
            crr.setReplaceString(replaceString);
        }

        tc.setColumnRenamingRule(crr);
    }

    private void parseJdbcConnection(CodeGenConfiguration context, Node node) {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();

        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        Properties attributes = parseAttributes(node);
        String driverClass = attributes.getProperty("driverClass"); //$NON-NLS-1$
        String connectionURL = attributes.getProperty("connectionURL"); //$NON-NLS-1$
        String userId = attributes.getProperty("userId"); //$NON-NLS-1$
        String password = attributes.getProperty("password"); //$NON-NLS-1$

        jdbcConnectionConfiguration.setDriverClass(driverClass);
        jdbcConnectionConfiguration.setConnectionURL(connectionURL);

        if (stringHasValue(userId)) {
            jdbcConnectionConfiguration.setUserId(userId);
        }

        if (stringHasValue(password)) {
            jdbcConnectionConfiguration.setPassword(password);
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseProperty(jdbcConnectionConfiguration, childNode);
            }
        }
    }

    private void parseClassPathEntry(CodeGenConfiguration configuration, Node node) {
        Properties attributes = parseAttributes(node);

        configuration.addClasspathEntry(attributes.getProperty("location")); //$NON-NLS-1$
    }

    private void parseProperty(PropertyHolder propertyHolder, Node node) {
        Properties attributes = parseAttributes(node);

        String name = attributes.getProperty("name"); //$NON-NLS-1$
        String value = attributes.getProperty("value"); //$NON-NLS-1$

        propertyHolder.addProperty(name, value);
    }

    private Properties parseAttributes(Node node) {
        Properties attributes = new Properties();
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node attribute = nnm.item(i);
            String value = parsePropertyTokens(attribute.getNodeValue());
            attributes.put(attribute.getNodeName(), value);
        }

        return attributes;
    }

    private String parsePropertyTokens(String string) {
        final String OPEN = "${"; //$NON-NLS-1$
        final String CLOSE = "}"; //$NON-NLS-1$

        String newString = string;
        if (newString != null) {
            int start = newString.indexOf(OPEN);
            int end = newString.indexOf(CLOSE);

            while (start > -1 && end > start) {
                String prepend = newString.substring(0, start);
                String append = newString.substring(end + CLOSE.length());
                String propName = newString.substring(start + OPEN.length(),
                        end);
                String propValue = properties.getProperty(propName);
                if (propValue != null) {
                    newString = prepend + propValue + append;
                }

                start = newString.indexOf(OPEN, end);
                end = newString.indexOf(CLOSE, end);
            }
        }

        return newString;
    }
}
