package com.ma.generator;

import com.ma.generator.config.CodeGenConfiguration;
import com.ma.generator.config.CodeGeneratorConfigParser;
import org.mybatis.generator.config.*;
import org.mybatis.generator.exception.XMLParserException;

import java.io.*;
import java.net.URL;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by lanux on 2016/4/22.
 */
public class GeneratorMain {

    public static void main(String[] args) {
        try {
            InputStream inputStream = getInputStream(args);
            CodeGeneratorConfigParser cp = new CodeGeneratorConfigParser();
            CodeGenConfiguration config = cp.parseConfiguration(inputStream);

            Configuration configuration = constructConfiguration(config);
            CodeGenerator generator = new CodeGenerator(configuration);
            generator.generate();

        } catch (XMLParserException e) {
            writeLine(getString("Progress.3")); //$NON-NLS-1$
            writeLine();
            for (String error : e.getErrors()) {
                writeLine(error);
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static InputStream getInputStream(String[] args) throws FileNotFoundException {
        InputStream inputStream = null;
        if (args != null && args.length > 0) {
            inputStream = new FileInputStream(new File(args[0].trim()));
        } else {
            String pathname = "../conf/code-generator-config.xml";
            File file = new File(pathname);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            }
        }
        if (inputStream == null) {
            inputStream = getResource("code-generator-config.xml");
            if (inputStream == null) {
                throw new FileNotFoundException("code-generator-config.xml");
            }
        }
        return inputStream;
    }

    public static InputStream getResource(String resource) {
        ClassLoader classLoader = GeneratorMain.class.getClassLoader();
        URL url = classLoader.getResource(resource);
        if (url != null) {
            try {
                return url.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(resource + " not found!");
        }
        InputStream inputStream = classLoader.getResourceAsStream(resource);
        if (inputStream == null) {
            System.out.println(resource + " not found");
        } else {
            System.out.println(resource + " found");
        }
        return inputStream;
    }

    private static Configuration constructConfiguration(CodeGenConfiguration config) {
        Configuration configuration = new Configuration();
        if (config.getClassPathEntries() != null) {
            config.getClassPathEntries().forEach(configuration::addClasspathEntry);
        }
        Context context = new Context(null);
        configuration.addContext(context);
        context.setId("code-generator");
        context.setTargetRuntime("MyBatis3");
        context.setJdbcConnectionConfiguration(config.getJdbcConnectionConfiguration());
        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
        context.getJdbcConnectionConfiguration().addProperty("remarks", "true");

        JavaTypeResolverConfiguration javaTypeResolverConifg = new JavaTypeResolverConfiguration();
        javaTypeResolverConifg.setConfigurationType("com.ma.generator.internal.types.JavaTypeResolverImpl");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConifg);

        CommentGeneratorConfiguration commentGenConfig = new CommentGeneratorConfiguration();
        commentGenConfig.addProperty("suppressAllComments", "false");
        commentGenConfig.addProperty("addRemarkComments", "true");
        context.setCommentGeneratorConfiguration(commentGenConfig);

        config.getTableConfigurations().forEach(e -> {
            TableConfiguration tc = new TableConfiguration(context);
            tc.setCatalog(e.getCatalog());
            tc.setTableName(e.getTableName());
            tc.setDomainObjectName(e.getDomainObjectName());
            tc.setSchema(e.getSchema());
            tc.addProperty("module", e.getModule());
            if (e.getExcludes() != null) {
                for (String excludeColumn : e.getExcludes()) {
                    tc.addIgnoredColumn(new IgnoredColumn(excludeColumn.trim()));
                }
            }
            context.addTableConfiguration(tc);
        });

        if (config.getTargetPackage() != null) {
            context.addProperty("targetPackage", config.getTargetPackage());
        }
        if (config.getTargetProject() != null) {
            context.addProperty("targetProject", config.getTargetProject());
        }
        return configuration;
    }

    private static void writeLine(String message) {
        System.out.println(message);
    }

    private static void writeLine() {
        System.out.println();
    }

}
