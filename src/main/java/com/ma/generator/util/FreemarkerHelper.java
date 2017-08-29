package com.ma.generator.util;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.*;

/**
 * Created by lanux on 2016/4/25.
 */
public class FreemarkerHelper {

    public static List<String> getAvailableAutoInclude(Configuration conf, List<String> autoIncludes) {
        List<String> results = new ArrayList();
        for (String autoInclude : autoIncludes) {
            try {
                Template t = new Template("__auto_include_test__", new StringReader("1"), conf);
                conf.setAutoIncludes(Arrays.asList(new String[] { autoInclude }));
                t.process(new HashMap(), new StringWriter());
                results.add(autoInclude);
            } catch (Exception e) {
            }
        }
        return results;
    }

    public static void processTemplate(Template template, Map model, File outputFile, String encoding)
            throws IOException, TemplateException {
        outputFile.getParentFile().mkdirs();
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), encoding));
        template.process(model, out);
        out.close();
    }

    public static String processTemplateString(String templateString, Map model, Configuration conf) {
        StringWriter out = new StringWriter();
        try {
            Template template = new Template("templateString...", new StringReader(templateString), conf);
            template.process(model, out);
            return out.toString();
        } catch (Exception e) {
            throw new IllegalStateException("cannot process templateString:" + templateString + " cause:" + e, e);
        }
    }

    public static Configuration getFreeMarkerCfg(String templatePath) {
        Configuration freemarkerCfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        freemarkerCfg.setBooleanFormat("true,false");
        freemarkerCfg.setNumberFormat("#");
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setClassicCompatible(true);
        try {
            freemarkerCfg.setTemplateLoader(createTemplateLoader(templatePath));
            //freemarkerCfg.setDirectoryForTemplateLoading(new File(ftlPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freemarkerCfg;
    }

    public static void generateFile(Configuration cfg, String templateFileName, Map model, File targetFile)
            throws Exception {
        Template t = cfg.getTemplate(templateFileName);
        processTemplate(t, model, targetFile, "UTF-8");
    }

    public static TemplateLoader createTemplateLoader(String templatePath) throws IOException {
        return InitParamParser.createTemplateLoader(templatePath, FreemarkerHelper.class.getClass());
    }

}
