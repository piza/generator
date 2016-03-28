package org.peter.generator.plugin;

import org.peter.generator.ServiceGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * Created by Peter on 16/1/25.
 */
public class MobileApiPlugin extends PluginAdapter {


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

//    private void generateToString(IntrospectedTable introspectedTable,
//                                  TopLevelClass topLevelClass) {
//        String tableName=introspectedTable.getTableConfiguration().getTableName();
//        StringBuilder sb=new StringBuilder();
//        sb.append("* {\n");
//        sb.append("    *   \"success\":{\n");
//        sb.append("    *      \"pagingInfo\":{\n");
//        sb.append("    *         \"page\":0,\n");
//        sb.append("    *         \"perPage\":10\n");
//        sb.append("    *         \"total\":21\n");
//        sb.append("    *         \"needPaging\":true\n");
//        sb.append("    *         \"start\":0\n");
//        sb.append("    *       },\n");
//        sb.append("    *       \"models\":[{\n");
//        for(Field field:topLevelClass.getFields()){
//            String property = field.getName();
//            sb.append("    *         ").append(property)
//                    .append(":")
//                    .append("\"")
//            .append(getRemark(property,introspectedTable))
//            .append("\",\n");
//
//        }
//        sb.append("    *        }\n    *       ]\n    *   }\n    * }");
//        ServiceGenerator.addModelJsonComment(tableName,sb.toString());
//    }
private void generateToString(IntrospectedTable introspectedTable,
                              TopLevelClass topLevelClass) {
    String tableName=introspectedTable.getTableConfiguration().getTableName();
    StringBuilder sb=new StringBuilder();
    sb.append(" {\n");
    for(Field field:topLevelClass.getFields()){
        String property = field.getName();
        sb.append("    *           ").append(property)
                .append(":")
                .append("\"")
                .append(getRemark(property,introspectedTable))
                .append("\",\n");

    }
    sb.append("    *           }");
    ServiceGenerator.addModelJsonComment(tableName,sb.toString());
}


    private String getRemark(String fieldName,IntrospectedTable introspectedTable){
        for(IntrospectedColumn column:introspectedTable.getAllColumns()){
            if(column.getJavaProperty().equals(fieldName)){
                return column.getRemarks()==null? "":column.getRemarks();
            }
        }
        return "";
    }


}
