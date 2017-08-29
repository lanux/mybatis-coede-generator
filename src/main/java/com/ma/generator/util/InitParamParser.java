package com.ma.generator.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core._ObjectBuilderSettingEvaluator;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.log.Logger;
import freemarker.template.utility.StringUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by lanux on 2016/4/25.
 */
public class InitParamParser {
    public static final String TEMPLATE_PATH_PREFIX_CLASS = "class://";
    public static final String TEMPLATE_PATH_PREFIX_CLASSPATH = "classpath:";
    public static final String TEMPLATE_PATH_PREFIX_FILE = "file://";
    public static final String TEMPLATE_PATH_SETTINGS_BI_NAME = "settings";

    private static final Logger LOG = Logger.getLogger("freemarker.servlet");

    private InitParamParser() {
        // Not to be instantiated
    }

    static TemplateLoader createTemplateLoader(
            String templatePath, Class classLoaderClass)
            throws IOException {
        final int settingAssignmentsStart = findTemplatePathSettingAssignmentsStart(templatePath);
        String pureTemplatePath = (settingAssignmentsStart == -1 ?
                templatePath :
                templatePath.substring(0, settingAssignmentsStart))
                .trim();

        TemplateLoader templateLoader = null;
        if (pureTemplatePath.startsWith(TEMPLATE_PATH_PREFIX_CLASS)) {
            String packagePath = pureTemplatePath.substring(TEMPLATE_PATH_PREFIX_CLASS.length());
            packagePath = normalizeToAbsolutePackagePath(packagePath);
            templateLoader = new ClassTemplateLoader(classLoaderClass, packagePath);
        } else if (pureTemplatePath.startsWith(TEMPLATE_PATH_PREFIX_CLASSPATH)) {
            // To be similar to Spring resource paths, we don't require "//":
            String packagePath = pureTemplatePath.substring(TEMPLATE_PATH_PREFIX_CLASSPATH.length());
            packagePath = normalizeToAbsolutePackagePath(packagePath);

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                LOG.warn("No Thread Context Class Loader was found. Falling back to the class loader of "
                        + classLoaderClass.getName() + ".");
                classLoader = classLoaderClass.getClassLoader();
            }

            templateLoader = new ClassTemplateLoader(classLoader, packagePath);
        } else if (pureTemplatePath.startsWith(TEMPLATE_PATH_PREFIX_FILE)) {
            String filePath = pureTemplatePath.substring(TEMPLATE_PATH_PREFIX_FILE.length());
            templateLoader = new FileTemplateLoader(new File(filePath));
        }

        if (settingAssignmentsStart != -1) {
            try {
                int nextPos = _ObjectBuilderSettingEvaluator.configureBean(
                        templatePath, templatePath.indexOf('(', settingAssignmentsStart) + 1, templateLoader,
                        _SettingEvaluationEnvironment.getCurrent());
                if (nextPos != templatePath.length()) {
                    throw new RuntimeException(
                            "Template path should end after the setting list in: "
                                    + templatePath);
                }
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to set properties in: " + templatePath, e);
            }
        }

        return templateLoader;
    }

    static String normalizeToAbsolutePackagePath(String path) {
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return "/" + path;
    }

    static List/*<String>*/ parseCommaSeparatedList(String value) throws ParseException {
        List/*<String>*/ valuesList = new ArrayList();
        String[] values = StringUtil.split(value, ',');
        for (int i = 0; i < values.length; i++) {
            final String s = values[i].trim();
            if (s.length() != 0) {
                valuesList.add(s);
            } else if (i != values.length - 1) {
                throw new ParseException("Missing list item berfore a comma", -1);
            }
        }
        return valuesList;
    }

    static List parseCommaSeparatedPatterns(String value) throws ParseException {
        List/*<String>*/ values = parseCommaSeparatedList(value);
        List/*<Pattern>*/ patterns = new ArrayList(values.size());
        for (int i = 0; i < values.size(); i++) {
            patterns.add(Pattern.compile((String) values.get(i)));
        }
        return patterns;
    }

    /**
     * This is like {@link #parseCommaSeparatedList(String)}, but is not confused by commas inside
     * {@code ?settings(...)} parts at the end of the items.
     */
    static List parseCommaSeparatedTemplatePaths(String commaSepItems) {
        List listItems;
        listItems = new ArrayList();
        while (commaSepItems.length() != 0) {
            int itemSettingAssignmentsStart = findTemplatePathSettingAssignmentsStart(commaSepItems);
            int pureItemEnd = itemSettingAssignmentsStart != -1 ? itemSettingAssignmentsStart : commaSepItems.length();
            int prevComaIdx = commaSepItems.lastIndexOf(',', pureItemEnd - 1);
            int itemStart = prevComaIdx != -1 ? prevComaIdx + 1 : 0;
            final String item = commaSepItems.substring(itemStart).trim();
            if (item.length() != 0) {
                listItems.add(0, item);
            } else if (listItems.size() > 0) {
                throw new RuntimeException(
                        "Missing list item before a comma");
            }
            commaSepItems = prevComaIdx != -1 ? commaSepItems.substring(0, prevComaIdx).trim() : "";
        }
        return listItems;
    }

    /**
     * @return -1 if there's no setting assignment.
     */
    static int findTemplatePathSettingAssignmentsStart(String s) {
        int pos = s.length() - 1;

        // Skip WS
        while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
            pos--;
        }

        // Skip `)`
        if (pos < 0 || s.charAt(pos) != ')')
            return -1;
        pos--;

        // Skip `(...`
        int parLevel = 1;
        int mode = 0;
        while (parLevel > 0) {
            if (pos < 0)
                return -1;
            char c = s.charAt(pos);
            switch (mode) {
            case 0:  // 0: outside string literal
                switch (c) {
                case '(':
                    parLevel--;
                    break;
                case ')':
                    parLevel++;
                    break;
                case '\'':
                    mode = 1;
                    break;
                case '"':
                    mode = 2;
                    break;
                }
                break;
            case 1:  // 1: inside '...'
                if (c == '\'' && !(pos > 0 && s.charAt(pos - 1) == '\\')) {
                    mode = 0;
                }
                break;
            case 2:  // 2: inside "..."
                if (c == '"' && !(pos > 0 && s.charAt(pos - 1) == '\\')) {
                    mode = 0;
                }
                break;
            }
            pos--;
        }

        // Skip WS
        while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
            pos--;
        }

        int biNameEnd = pos + 1;

        // Skip name chars
        while (pos >= 0 && Character.isJavaIdentifierPart(s.charAt(pos))) {
            pos--;
        }

        int biNameStart = pos + 1;
        if (biNameStart == biNameEnd) {
            return -1;
        }
        String biName = s.substring(biNameStart, biNameEnd);

        // Skip WS
        while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
            pos--;
        }

        // Skip `?`
        if (pos < 0 || s.charAt(pos) != '?')
            return -1;

        if (!biName.equals(TEMPLATE_PATH_SETTINGS_BI_NAME)) {
            throw new RuntimeException(
                    StringUtil.jQuote(biName) + " is unexpected after the \"?\". "
                            + "Expected \"" + TEMPLATE_PATH_SETTINGS_BI_NAME + "\".");
        }

        return pos;
    }

    private static final class TemplatePathParsingException extends RuntimeException {

        public TemplatePathParsingException(String message, Throwable cause) {
            super(message, cause);
        }

        public TemplatePathParsingException(String message) {
            super(message);
        }

    }
}
