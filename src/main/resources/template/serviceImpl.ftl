<#assign pojoClassName="${targetPackage}.${module}.pojo.${table.tableConfiguration.domainObjectName}">
<#assign pojoClassSimpleName="${table.tableConfiguration.domainObjectName}">
package ${targetPackage}.${module}.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import ${pojoClassName};
import ${targetPackage}.${module}.pojo.${pojoClassSimpleName}Mapper;
import ${targetPackage}.${module}.service.${pojoClassSimpleName}Service;

/**
* created on ${.now}
*/
@Service
public class ${pojoClassSimpleName}ServiceImpl implements ${pojoClassSimpleName}Service {

    @Autowired
    private ${pojoClassSimpleName}Mapper ${pojoClassSimpleName?uncap_first}Mapper;

}