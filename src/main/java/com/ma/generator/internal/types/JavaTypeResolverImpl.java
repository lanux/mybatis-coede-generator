package com.ma.generator.internal.types;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.math.BigDecimal;
import java.sql.Types;

/**
 * Created by lanux on 2016/4/22.
 */
public class JavaTypeResolverImpl extends JavaTypeResolverDefaultImpl {

    public FullyQualifiedJavaType calculateJavaType(
            IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType answer;
        JdbcTypeInformation jdbcTypeInformation = typeMap
                .get(introspectedColumn.getJdbcType());
        if (jdbcTypeInformation == null) {
            switch (introspectedColumn.getJdbcType()) {
            case Types.DECIMAL:
            case Types.NUMERIC:
                if (forceBigDecimals) {
                    answer = new FullyQualifiedJavaType(BigDecimal.class
                            .getName());
                    break;
                }
                if (introspectedColumn.getScale() > 0
                        || introspectedColumn.getLength() > 18) {
                    answer = new FullyQualifiedJavaType(Double.class
                            .getName());
                } else if (introspectedColumn.getLength() > 9) {
                    answer = new FullyQualifiedJavaType(Long.class.getName());
                    //} else if (introspectedColumn.getLength() > 4) {
                    // answer = new FullyQualifiedJavaType(Integer.class.getName());
                } else {
                    answer = new FullyQualifiedJavaType(Integer.class.getName());
                }
                break;

            default:
                answer = null;
                break;
            }
        } else {
            answer = jdbcTypeInformation.getFullyQualifiedJavaType();
        }

        return answer;
    }
}
