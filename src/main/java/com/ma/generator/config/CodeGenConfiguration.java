package com.ma.generator.config;

import org.mybatis.generator.config.JDBCConnectionConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by lanux on 2016/4/22.
 */
public class CodeGenConfiguration {
    private JDBCConnectionConfiguration jdbcConnectionConfiguration;

    private ArrayList<TableConfig> tableConfigurations;

    private String targetPackage;

    private String targetProject;

    private List<String> classPathEntries;

    private Properties properties = new Properties();

    public CodeGenConfiguration() {
    }

    public JDBCConnectionConfiguration getJdbcConnectionConfiguration() {
        return jdbcConnectionConfiguration;
    }

    public void setJdbcConnectionConfiguration(
            JDBCConnectionConfiguration jdbcConnectionConfiguration) {
        this.jdbcConnectionConfiguration = jdbcConnectionConfiguration;
    }

    public ArrayList<TableConfig> getTableConfigurations() {
        return tableConfigurations;
    }

    public void setTableConfigurations(ArrayList<TableConfig> tableConfigurations) {
        this.tableConfigurations = tableConfigurations;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public String getTargetProject() {
        return targetProject;
    }

    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    public List<String> getClassPathEntries() {
        return classPathEntries;
    }

    public void setClassPathEntries(List<String> classPathEntries) {
        this.classPathEntries = classPathEntries;
    }

    public void addTableConfiguration(TableConfig tc) {
        if (tableConfigurations == null) {
            tableConfigurations = new ArrayList<>();
        }
        tableConfigurations.add(tc);
    }

    public void addClasspathEntry(String location) {
        if (classPathEntries == null) {
            classPathEntries = new ArrayList<>();
        }
        classPathEntries.add(location);
    }

    public void addProperty(String name, String value) {
        if (properties == null) {
            properties = new Properties();
        }
        properties.put(name, value);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
