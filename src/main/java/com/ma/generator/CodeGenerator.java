package com.ma.generator;

import com.ma.generator.util.FreemarkerHelper;
import com.ma.generator.util.InitParamParser;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import static org.mybatis.generator.internal.util.ClassloaderUtility.getCustomClassloader;

/**
 * Created by lanux on 2016/4/25.
 */
public class CodeGenerator {

    private Configuration configuration;
    private List<String> warnings;

    public CodeGenerator(Configuration configuration) throws InvalidConfigurationException {
        this.configuration = configuration;
        this.warnings = new ArrayList<String>();
    }

    public void generate() throws Exception {

        ProgressCallback callback = new NullProgressCallback();

        if (configuration.getClassPathEntries().size() > 0) {
            ClassLoader classLoader = getCustomClassloader(configuration.getClassPathEntries());
            ObjectFactory.addExternalClassLoader(classLoader);
        }

        for (Context context : configuration.getContexts()) {
            context.introspectTables(callback, warnings, null);
            generateFiles(context);
        }

        callback.done();
    }

    private void generateFiles(Context context)
            throws Exception {
        Field introspectedTablesField = context.getClass().getDeclaredField("introspectedTables");
        introspectedTablesField.setAccessible(true);
        List<IntrospectedTable> introspectedTables = (List<IntrospectedTable>) introspectedTablesField.get(context);
        if (introspectedTables != null) {
            String ftlPath = InitParamParser.TEMPLATE_PATH_PREFIX_CLASS + "template";
            freemarker.template.Configuration freeMarkerCfg = FreemarkerHelper
                    .getFreeMarkerCfg(ftlPath);
            File directory = new DefaultShellCallback(true)
                    .getDirectory(context.getProperty("targetProject"), context.getProperty("targetPackage"));
            HashMap<Object, Object> model = new HashMap<>();
            model.putAll(context.getProperties());
            for (IntrospectedTable introspectedTable : introspectedTables) {
                model.putAll(introspectedTable.getTableConfiguration().getProperties());
                model.put("table", introspectedTable);
                List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
                model.put("pojoImportList", getImportList(allColumns));
                String module = (String) model.get("module");
                String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
                FreemarkerHelper
                        .generateFile(freeMarkerCfg, "mapper.ftl", model,
                                new File(getMapperFilePath(directory.getPath(), module, domainObjectName)));
                FreemarkerHelper
                        .generateFile(freeMarkerCfg, "dao.ftl", model,
                                new File(getDaoFilePath(directory.getPath(), module, domainObjectName)));
                FreemarkerHelper
                        .generateFile(freeMarkerCfg, "pojo.ftl", model,
                                new File(getPojoFilePath(directory.getPath(), module, domainObjectName)));
                FreemarkerHelper
                        .generateFile(freeMarkerCfg, "service.ftl", model,
                                new File(getServiceFilePath(directory.getPath(), module, domainObjectName)));
                FreemarkerHelper
                        .generateFile(freeMarkerCfg, "serviceImpl.ftl", model,
                                new File(getServiceImplFilePath(directory.getPath(), module, domainObjectName)));
            }
        }
    }

    private String getServiceFilePath(String path, String module, String domainObjectName) {
        StringBuilder sb = new StringBuilder(path);
        sb.append(File.separator);
        sb.append(module);
        sb.append(File.separator);
        sb.append("service");
        sb.append(File.separator);
        sb.append(domainObjectName);
        sb.append("Service.java");
        return sb.toString();
    }

    private String getPojoFilePath(String path, String module, String domainObjectName) {
        StringBuilder sb = new StringBuilder(path);
        sb.append(File.separator);
        sb.append(module);
        sb.append(File.separator);
        sb.append("pojo");
        sb.append(File.separator);
        sb.append(domainObjectName);
        sb.append(".java");
        return sb.toString();
    }

    private String getDaoFilePath(String path, String module, String domainObjectName) {
        StringBuilder sb = new StringBuilder(path);
        sb.append(File.separator);
        sb.append(module);
        sb.append(File.separator);
        sb.append("dao");
        sb.append(File.separator);
        sb.append(domainObjectName);
        sb.append("Mapper.java");
        return sb.toString();
    }

    private String getServiceImplFilePath(String path, String module, String domainObjectName) {
        StringBuilder sb = new StringBuilder(path);
        sb.append(File.separator);
        sb.append(module);
        sb.append(File.separator);
        sb.append("service");
        sb.append(File.separator);
        sb.append("impl");
        sb.append(File.separator);
        sb.append(domainObjectName);
        sb.append("ServiceImpl.java");
        return sb.toString();
    }

    private String getMapperFilePath(String path, String module, String domainObjectName) {
        StringBuilder sb = new StringBuilder(path);
        sb.append(File.separator);
        sb.append(module);
        sb.append(File.separator);
        sb.append("dao");
        sb.append(File.separator);
        sb.append("mapper");
        sb.append(File.separator);
        sb.append(domainObjectName);
        sb.append("Mapper.xml");
        return sb.toString();
    }

    /**
     * 表中所有列使用的import,Set转为List是由于freemarker遍历set会报错
     *
     * @return
     */
    public List<String> getImportList(List<IntrospectedColumn> allColumns) {
        Set<String> set = new HashSet<String>();
        for (IntrospectedColumn column : allColumns) {
            if (column != null) {
                FullyQualifiedJavaType javaType = column.getFullyQualifiedJavaType();
                if (javaType != null) {
                    List<String> columnImportList = javaType.getImportList();
                    if (columnImportList != null) {
                        set.addAll(columnImportList);
                    }
                }
            }
        }
        List<String> answer = new ArrayList<String>();
        if (set.size() > 0) {
            answer.addAll(set);
        }
        return answer;
    }
}
