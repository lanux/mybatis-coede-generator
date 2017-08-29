<?xml version="1.0" encoding="UTF-8"?>
<#assign pojoClassName="${targetPackage}.${module}.pojo.${table.tableConfiguration.domainObjectName}">
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${targetPackage}.${module}.dao.${table.tableConfiguration.domainObjectName}Mapper">
    <resultMap id="BaseResultMap" type="${pojoClassName}">
    <#list  table.primaryKeyColumns as column>
        <id column="${column.actualColumnName}" property="${column.javaProperty}" jdbcType="${column.jdbcTypeName}"/>
    </#list>
    <#list table.baseColumns as column>
        <result column="${column.actualColumnName}" property="${column.javaProperty}" jdbcType="${column.jdbcTypeName}"/>
    </#list>
    </resultMap>
    <#if (table.blobColumns?? && table.blobColumns?size>0) >
    <resultMap id="ResultMapWithBLOBs" type="${pojoClassName}" extends="BaseResultMap">
        <#list table.blobColumns as column>
            <result column="${column.actualColumnName}" property="${column.javaProperty}" jdbcType="${column.jdbcTypeName}"/>
        </#list>
    </resultMap>
    </#if>

    <sql id="BaseColumnList">
        <#list table.primaryKeyColumns as column>${column.actualColumnName},</#list><#list table.baseColumns as column>${column.actualColumnName}<#if column_has_next>,</#if></#list>
    </sql>
    <#if (table.blobColumns?? && table.blobColumns?size>0) >
    <sql id="BlobColumnList">
        <#list table.blobColumns as column>${column.actualColumnName}<#if column_has_next>,</#if></#list>
    </sql>
    </#if>

    <sql id="WhereClause">
        <where>
            <trim prefixOverrides="AND" >
            <#list table.allColumns as column>
                <if test="${column.javaProperty} != null">
                    AND ${column.actualColumnName} = <#noparse>#{</#noparse>${column.javaProperty},jdbcType=${column.jdbcTypeName}}
                </if>
            </#list>
            </trim>
        </where>
    </sql>

    <select id="selectByPrimaryKey" parameterType="${table.primaryKeyColumns[0].fullyQualifiedJavaType.fullyQualifiedName}" resultMap="BaseResultMap">
        select
        <include refid="BaseColumnList"/>
        from ${table.tableConfiguration.tableName}
    <#list table.primaryKeyColumns as column>
        where ${column.actualColumnName}<#noparse>=#{${column.javaProperty},jdbcType=${column.jdbcTypeName}}</#noparse>
    </#list>
    </select>
    <#if (table.blobColumns?? && table.blobColumns?size>0) >
    <select id="selectByPrimaryKeyWithBlobs" parameterType="${table.primaryKeyColumns[0].fullyQualifiedJavaType.fullyQualifiedName}" resultMap="ResultMapWithBLOBs">
        select
        <include refid="BaseColumnList"/>,<include refid="BlobColumnList"/>
        from ${table.tableConfiguration.tableName}
        <#list table.primaryKeyColumns as column>
            where ${column.actualColumnName}<#noparse>=#{${column.javaProperty},jdbcType=${column.jdbcTypeName}}</#noparse>
        </#list>
    </select>
    </#if>

    <insert id="insertSelective" parameterType="${pojoClassName}">
        insert into ${table.tableConfiguration.tableName}(
        <#list  table.allColumns as column>
            <if test="${column.javaProperty} != null">
            ${column.actualColumnName}<#if column_has_next>,</#if>
            </if>
        </#list>
        )values(
        <#list  table.allColumns as column>
            <if test="${column.javaProperty} != null">
            <#noparse>#{</#noparse>${column.javaProperty},javaType=${column.jdbcTypeName}<#noparse>}</#noparse><#if column_has_next>,</#if>
            </if>
        </#list>
        )
    </insert>

    <update id="updateByPrimaryKeySelective" parameterType="${pojoClassName}">
        update ${table.tableConfiguration.tableName} set
    <#list  table.baseColumns as column>
        <if test="${column.javaProperty} != null">
        ${column.actualColumnName}=<#noparse>#{</#noparse>${column.javaProperty},javaType=${column.jdbcTypeName}<#noparse>}</#noparse><#if column_has_next>,</#if>
        </if>
    </#list>
        where
    <#list  table.primaryKeyColumns as column>
        <#if test="column_index != 0">AND </#if>${column.actualColumnName}=<#noparse>#{</#noparse>${column.javaProperty},javaType=${column.jdbcTypeName}<#noparse>}</#noparse>
    </#list>
    </update>

    <delete id="deleteByPrimaryKey" parameterType="${table.primaryKeyColumns[0].fullyQualifiedJavaType.fullyQualifiedName}">
        delete from ${table.tableConfiguration.tableName} where
    <#list table.primaryKeyColumns as column>
        ${column.actualColumnName}<#noparse>=#{javaProperty,jdbcType=${column.jdbcTypeName}}</#noparse>
    </#list>
    </delete>

    <select id="selectByExample" parameterType="${pojoClassName}" resultMap="BaseResultMap">
        select
        <include refid="BaseColumnList"/>
        from ${table.tableConfiguration.tableName}
        <include refid="WhereClause"/>
    </select>

    <select id="countByExample" parameterType="${pojoClassName}" resultType="java.lang.Integer">
        select count(1)
        from ${table.tableConfiguration.tableName}
        <include refid="WhereClause"/>
    </select>
</mapper>