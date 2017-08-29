package com.ma.generator.config;

import org.mybatis.generator.config.PropertyHolder;

import java.util.Set;

/**
 * Created by lanux on 2016/4/22.
 */
public class TableConfig extends PropertyHolder {
    private Set<String> excludes;
    private Set<String> includes;
    private String module;
    private String schema;
    private String tableName;
    private String domainObjectName;
    private String catalog;

    public TableConfig() {
        super();
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDomainObjectName() {
        return domainObjectName;
    }

    public void setDomainObjectName(String domainObjectName) {
        this.domainObjectName = domainObjectName;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public boolean isColumnIgnored(String actualColumnName) {
        return this.excludes != null && this.excludes.contains(actualColumnName);
    }
}
