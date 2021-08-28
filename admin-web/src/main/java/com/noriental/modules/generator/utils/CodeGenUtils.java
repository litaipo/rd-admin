/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.noriental.modules.generator.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.noriental.common.exception.RRException;
import com.noriental.modules.generator.entity.ColumnEntity;
import com.noriental.modules.generator.entity.GenConfig;
import com.noriental.modules.generator.entity.TableEntity;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 * copy elunez/eladmin/blob/master/eladmin-generator/src/main/java/me/zhengjie/utils/GenUtil.java
 *
 * @author Jing.Li
 * @date 2020-03-13
 */
@Slf4j
@UtilityClass
public class CodeGenUtils {
    private final String BASE_REQUEST_VM = "BaseRequest.java.vm";
    private final String BASE_RESPONSE_VM = "BaseResponse.java.vm";
    private final String PAGE_REQUEST_VM = "PageEntityRequest.java.vm";
    private final String PAGE_RESPONSE_VM = "PageEntityResponse.java.vm";
    private final String ENTITY_JAVA_VM = "Entity.java.vm";
    private final String ENTITY_PAGE_JAVA_VM = "EntityPage.java.vm";
    private final String MAPPER_JAVA_VM = "Mapper.java.vm";
    private final String DAO_JAVA_VM = "Mapper.java.vm";
    private final String SERVICE_JAVA_VM = "Service.java.vm";
    private final String SERVICE_IMPL_JAVA_VM = "ServiceImpl.java.vm";
    private final String CONTROLLER_JAVA_VM = "Controller.java.vm";
    private final String MAPPER_XML_VM = "Mapper.xml.vm";
    private final String DAO_XML_VM = "Mapper.xml.vm";
    private final String MENU_SQL_VM = "menu.sql.vm";
    private final String VUE_INDEX_VM = "index.vue.vm";
    private final String VUE_OPERATE_VM = "add-or-update.vue.vm";

    /**
     * 配置
     *
     * @return
     */
    private List<String> getTemplates(int generateTarget) {
        // 0 默认 1 okay
        List<String> templates = new ArrayList<>();
        if (1 == generateTarget) {
            templates.add("template/ok/BaseRequest.java.vm");
            templates.add("template/ok/BaseResponse.java.vm");
            templates.add("template/ok/EntityPage.java.vm");
            templates.add("template/ok/Entity.java.vm");
            templates.add("template/ok/Mapper.java.vm");
            templates.add("template/ok/Mapper.xml.vm");
            templates.add("template/ok/Service.java.vm");
            templates.add("template/ok/ServiceImpl.java.vm");
            templates.add("template/ok/Controller.java.vm");
            return templates;
        }
        templates.add("template/Entity.java.vm");
        templates.add("template/EntityPage.java.vm");
        templates.add("template/Mapper.java.vm");
        templates.add("template/Mapper.xml.vm");
        templates.add("template/Service.java.vm");
        templates.add("template/ServiceImpl.java.vm");
        templates.add("template/Controller.java.vm");
        templates.add("template/menu.sql.vm");
        templates.add("template/index.vue.vm");
        templates.add("template/add-or-update.vue.vm");
        return templates;
    }

    /**
     * 列名转换成Java属性名
     */
    public String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    private String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new RRException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    private String getFileName(String template, String caseClassName, String packageName, String moduleName) {
        String packagePath = "main" + File.separator + "java" + File.separator;

        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }

        if (template.contains(BASE_REQUEST_VM)) {
            return packagePath + "bean" + File.separator + "request" + File.separator + caseClassName + "BaseRequest.java";
        }
        if (template.contains(PAGE_REQUEST_VM)) {
            return packagePath + "bean" + File.separator + "request" + File.separator + "PageEntityRequest.java";
        }

        if (template.contains(BASE_RESPONSE_VM)) {
            return packagePath + "bean" + File.separator + "response" + File.separator + caseClassName + "BaseResponse.java";
        }

        if (template.contains(PAGE_RESPONSE_VM)) {
            return packagePath + "bean" + File.separator + "request" + File.separator + "PageEntityResponse.java";
        }

        if (template.contains(ENTITY_JAVA_VM)) {
            return packagePath + "entity" + File.separator + caseClassName + ".java";
        }

        if (template.contains(ENTITY_PAGE_JAVA_VM)) {
            return packagePath + "entity" + File.separator + caseClassName + "PageEntity.java";
        }

        if (template.contains(MAPPER_JAVA_VM)) {
            return packagePath + "mapper" + File.separator + caseClassName + "Mapper.java";
        }

        if (template.contains(DAO_JAVA_VM)) {
            return packagePath + "mapper" + File.separator + caseClassName + "Dao.java";
        }

        if (template.contains(SERVICE_JAVA_VM)) {
            return packagePath + "service" + File.separator + caseClassName + "Service.java";
        }

        if (template.contains(SERVICE_IMPL_JAVA_VM)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + caseClassName + "ServiceImpl.java";
        }

        if (template.contains(CONTROLLER_JAVA_VM)) {
            return packagePath + "controller" + File.separator + caseClassName + "Controller.java";
        }

        if (template.contains(MAPPER_XML_VM)) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + moduleName + File.separator + caseClassName + "Mapper.xml";
        }

        if (template.contains(DAO_XML_VM)) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + moduleName + File.separator + caseClassName + "Dao.xml";
        }

        if (template.contains(MENU_SQL_VM)) {
            return caseClassName.toLowerCase() + "_menu.sql";
        }

        if (template.contains(VUE_INDEX_VM)) {
            return "main" + File.separator + "resources" + File.separator + "src" + File.separator + "views" + File.separator + "modules" +
                    File.separator + moduleName + File.separator + caseClassName.toLowerCase() + ".vue";
        }

        if (template.contains(VUE_OPERATE_VM)) {
            return "main" + File.separator + "resources" + File.separator + "src" + File.separator + "views" + File.separator + "modules" +
                    File.separator + moduleName + File.separator + caseClassName.toLowerCase() + "-add-or-update.vue";
        }
        return null;
    }

    private static String splitInnerName(String name) {
        name = name.replaceAll("\\.", "_");
        return name;
    }

    /**
     * 生成代码
     */
    @SneakyThrows
    public void generatorCode(GenConfig genConfig, Map<String, String> table,
                              List<Map<String, String>> columns, ZipOutputStream zip) {
        //配置信息
        Configuration config = getConfig();
        boolean hasBigDecimal = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));
        String tablePrefix;
        if (StrUtil.isNotBlank(genConfig.getTablePrefix())) {
            tablePrefix = genConfig.getTablePrefix();
        } else {
            tablePrefix = config.getString("tablePrefix");
        }
//        Integer generateTarget = genConfig.getGenerateTarget() == null ? config.getInt("generate_target") : genConfig.getGenerateTarget();
        Integer generateTarget = 1;

        //表名转换成Java类名
        String caseClassName = tableToJava(tableEntity.getTableName(), tablePrefix);
        tableEntity.setCaseClassName(caseClassName);
        tableEntity.setLowerClassName(StringUtils.uncapitalize(caseClassName));

        //列信息
        List<ColumnEntity> columnList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));
            columnEntity.setNullable("NO".equals(column.get("isNullable")));
            columnEntity.setColumnType(column.get("columnType"));
            columnEntity.setHidden(Boolean.FALSE);
            //列名转换成Java属性名
            String lowerAttrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setCaseAttrName(lowerAttrName);
            columnEntity.setLowerAttrName(StringUtils.uncapitalize(lowerAttrName));
            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columnList.add(columnEntity);
        }
        tableEntity.setColumns(columnList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        Map<String, Object> map = getTemplateData(genConfig, config, hasBigDecimal, tableEntity);
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates(generateTarget);
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, CharsetUtil.UTF_8);
            tpl.merge(context, sw);

            //添加到zip
            zip.putNextEntry(new ZipEntry(Objects
                    .requireNonNull(getFileName(template, tableEntity.getCaseClassName()
                            , map.get("package").toString(), map.get("moduleName").toString()))));
            IoUtil.write(zip, StandardCharsets.UTF_8, false, sw.toString());
            IoUtil.close(sw);
            zip.closeEntry();
        }
    }

    /**
     * 封装模版数据
     *
     * @param genConfig
     * @param config
     * @param hasBigDecimal
     * @param tableEntity
     * @return
     */
    private static Map<String, Object> getTemplateData(GenConfig genConfig, Configuration config, boolean hasBigDecimal, TableEntity tableEntity) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName", tableEntity.getTableName());
        map.put("pk", tableEntity.getPk());
        map.put("caseClassName", tableEntity.getCaseClassName());
        map.put("lowerClassName", tableEntity.getLowerClassName());
        map.put("pathName", tableEntity.getLowerClassName().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("datetime", DateUtil.now());
        map.put("email", config.getString("email"));

        if (StrUtil.isNotBlank(genConfig.getComments())) {
            map.put("comments", genConfig.getComments());
        } else {
            map.put("comments", tableEntity.getComments());
        }
        if (StrUtil.isNotBlank(genConfig.getAuthor())) {
            map.put("author", genConfig.getAuthor());
        } else {
            map.put("author", config.getString("author"));
        }
        if (StrUtil.isNotBlank(genConfig.getModuleName())) {
            map.put("moduleName", genConfig.getModuleName());
        } else {
            map.put("moduleName", config.getString("moduleName"));
        }

        if (StrUtil.isNotBlank(genConfig.getPackageName())) {
            map.put("package", genConfig.getPackageName());
            // com.noriental.modules
            String[] pathStr = genConfig.getPackageName().split("\\." );
            String mainPath = pathStr[0];
            if (pathStr.length > 1) {
                mainPath = pathStr[0] + "." + pathStr[1];
            }
            map.put("mainPath", mainPath);
        } else {
            map.put("package", config.getString("package"));
            map.put("mainPath", config.getString("mainPath"));
        }
        return map;
    }
}
