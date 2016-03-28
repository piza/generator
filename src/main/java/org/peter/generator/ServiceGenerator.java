package org.peter.generator;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.TableConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Peter on 15/10/24.
 */
public class ServiceGenerator implements ProgressCallback {

    private Properties properties;
    private Configuration config;
    private String outputRootPath;
    private String baseOutputPath;
    private String modelPackage="";
    private String daoPackage="";
    private String servicePackage="";
    private String validatorPackage="";
    private String controllerPackage="";
    private String apiPackage="";

    private VelocityContext context;
    private VelocityEngine ve;
    private Template mapperTemplate;
    private Template serviceTemplate;
    private Template implTemplate;
    private Template validatorTemplate;
    private Template controllerTemplate;
    private Template apiTemplate;

    private static Map<String,String> commentMap=new HashMap<String,String>();


    public static void addModelJsonComment(String tableName,String jsonStr){
        commentMap.put(tableName,jsonStr);
    }
    public ServiceGenerator(Properties properties,Configuration config){
        this.properties=properties;
        this.config=config;
        outputRootPath=properties.getProperty("outputPath");
        baseOutputPath=properties.getProperty("outputPath")+File.separator+properties.getProperty("basePackagePath");
        modelPackage=properties.getProperty("basePackage")+".model";
        daoPackage=properties.getProperty("basePackage")+".dao";
        servicePackage=properties.getProperty("basePackage")+".service";
        validatorPackage=properties.getProperty("basePackage")+".validator";
        controllerPackage=properties.getProperty("basePackage")+".controller";
        apiPackage=properties.getProperty("basePackage")+".api";

        context = new VelocityContext();
        context.put("basePackage",properties.getProperty("basePackage"));
        context.put("modelPackage",modelPackage );
        context.put("daoPackage", daoPackage);
        context.put("servicePackage",servicePackage);
        context.put("validatorPackage",validatorPackage);
        context.put("controllerPackage",controllerPackage);
        context.put("apiPackage",apiPackage);


        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        mapperTemplate = ve.getTemplate("mapperTemplate.vm");
        serviceTemplate = ve.getTemplate("serviceTemplate.vm");
        implTemplate = ve.getTemplate("serviceImplTemplate.vm");
        validatorTemplate = ve.getTemplate("validatorTemplate.vm");
        controllerTemplate = ve.getTemplate("controllerTemplate.vm");
        apiTemplate = ve.getTemplate("apiTemplate.vm","UTF-8");


    }
    @Override
    public void introspectionStarted(int i) {
        System.out.println("introspectionStarted:"+i);

    }

    @Override
    public void generationStarted(int i) {
        System.out.println("generationStarted:"+i);
        try {
            File resFolder=new File(outputRootPath+File.separator+"resources");
            FileUtils.deleteDirectory(resFolder);
            resFolder.mkdirs();
            File srcFolder=new File(outputRootPath+File.separator+"src");
            FileUtils.deleteDirectory(srcFolder);
            File daoFolder=new File(baseOutputPath+File.separator+"dao_generics");
            if(!daoFolder.exists()){
                daoFolder.mkdirs();
            }
            File serviceFolder=new File(baseOutputPath+File.separator+"service");
            if(!serviceFolder.exists()){
                serviceFolder.mkdirs();
            }
            File serviceImplFolder=new File(baseOutputPath+File.separator+"service"+File.separator+"impl");
            if(!serviceImplFolder.exists()){
                serviceImplFolder.mkdirs();
            }

            File validatorFolder=new File(baseOutputPath+File.separator+"validator");
            if(!validatorFolder.exists()){
                validatorFolder.mkdirs();
            }
            File controllerFolder=new File(baseOutputPath+File.separator+"controller");
            if(!controllerFolder.exists()){
                controllerFolder.mkdirs();
            }

            File apiFolder=new File(baseOutputPath+File.separator+"api");
            if(!apiFolder.exists()){
                apiFolder.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveStarted(int i) {
        System.out.println("saveStarted:"+i);
    }

    @Override
    public void startTask(String s) {
        System.out.println("startTask:"+s);

    }

    @Override
    public void done() {

        List<TableConfiguration> tableConfigurationList=config.getContext("defaultContext").getTableConfigurations();

        for(TableConfiguration tableConfiguration:tableConfigurationList){
            generateFile(tableConfiguration.getDomainObjectName(),tableConfiguration.getTableName());
        }

        Runtime runtime=Runtime.getRuntime();
        String osName=System.getProperty("os.name").toLowerCase();
        if(osName.indexOf("mac")>=0){
            try {
                runtime.exec("open "+outputRootPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(osName.indexOf("windows")>=0){
            try {
                runtime.exec("start "+outputRootPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String lowFirstChar(String tempStr){
        char[] cs=tempStr.toCharArray();
        cs[0]+=32;
        return String.valueOf(cs);
    }

    private void generateFile(String modelClass,String tableName){

        try {
            context.put("modelClass", modelClass);
            context.put("modelClassParam", lowFirstChar(modelClass));

            FileWriter mapperWriter=new FileWriter(baseOutputPath+File.separator+"dao_generics"+File.separator+modelClass+"Mapper.java");
            mapperTemplate.merge(context, mapperWriter);
            mapperWriter.flush();
            mapperWriter.close();


            FileWriter fileWriter=new FileWriter(baseOutputPath+File.separator+"service"+File.separator+modelClass+"Service.java");
            serviceTemplate.merge(context, fileWriter);
            fileWriter.flush();
            fileWriter.close();

            FileWriter implFileWriter=new FileWriter(baseOutputPath+File.separator+"service"+File.separator+"impl"+File.separator+modelClass+"ServiceImpl.java");
            implTemplate.merge(context,implFileWriter);
            implFileWriter.flush();
            implFileWriter.close();

            FileWriter validatorWriter=new FileWriter(baseOutputPath+File.separator+"validator"+File.separator+modelClass+"Validator.java");
            validatorTemplate.merge(context, validatorWriter);
            validatorWriter.flush();
            validatorWriter.close();

            FileWriter controllerWriter=new FileWriter(baseOutputPath+File.separator+"controller"+File.separator+modelClass+"Controller.java");
            controllerTemplate.merge(context, controllerWriter);
            controllerWriter.flush();
            controllerWriter.close();


            context.put("jsonComment",commentMap.get(tableName));
            FileWriter apiWriter=new FileWriter(baseOutputPath+File.separator+"api"+File.separator+modelClass+"API.java");
            apiTemplate.merge(context, apiWriter);
            apiWriter.flush();
            apiWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void checkCancel() throws InterruptedException {
        System.out.println("checkCancel");

    }
}
