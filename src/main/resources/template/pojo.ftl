package ${targetPackage}.${module}.pojo;

<#list  pojoImportList as importStr>
import ${importStr};
</#list>

/**
* tableName:${table.fullyQualifiedTable.introspectedTableName}
* ${table.remarks}
* created on ${.now}
*/
public class ${table.tableConfiguration.domainObjectName} implements Serializable {

    private static final long serialVersionUID = 1L;

    <#list  table.allColumns as column>

    private ${column.fullyQualifiedJavaType.shortName} ${column.javaProperty}; <#if (column.remarks)??>//${column.remarks}</#if>

    </#list>
    public ${table.tableConfiguration.domainObjectName}(){
    }

    <#list  table.allColumns as column>
    public ${column.fullyQualifiedJavaType.shortName} get${column.javaProperty?cap_first}(){
        return this.${column.javaProperty};
    }

    public void set${column.javaProperty?cap_first}(${column.javaProperty?cap_first} ${column.javaProperty}){
        this.${column.javaProperty} = ${column.javaProperty};
    }

    </#list>
}