<#assign pojoClassName="${targetPackage}.${module}.pojo.${table.tableConfiguration.domainObjectName}">
<#assign pojoClassSimpleName="${table.tableConfiguration.domainObjectName}">
package ${targetPackage}.${module}.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import ${pojoClassName};

/**
* created on ${.now}
*/
public interface ${table.tableConfiguration.domainObjectName}Mapper {

    int insertSelective(${pojoClassSimpleName} ${pojoClassSimpleName?uncap_first});

    deleteByPrimaryKey(${table.primaryKeyColumns[0].fullyQualifiedJavaType.shortName} ${table.primaryKeyColumns[0].javaProperty});

    ${pojoClassSimpleName} selectByPrimaryKey(${table.primaryKeyColumns[0].fullyQualifiedJavaType.shortName} ${table.primaryKeyColumns[0].javaProperty});

    int updateByPrimaryKeySelective(${pojoClassSimpleName} ${pojoClassSimpleName?uncap_first});

    int countByExample(${pojoClassSimpleName} ${pojoClassSimpleName?uncap_first});

    List<${pojoClassSimpleName}> selectByExample(${pojoClassSimpleName} ${pojoClassSimpleName?uncap_first});

    <#if (table.blobColumns?? && table.blobColumns?size>0) >
    int selectByPrimaryKeyWithBlobs(${table.primaryKeyColumns[0].fullyQualifiedJavaType.shortName} ${table.primaryKeyColumns[0].javaProperty});
    </#if>
}

