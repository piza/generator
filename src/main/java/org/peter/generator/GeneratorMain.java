package org.peter.generator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Peter on 15/10/24.
 */
public class GeneratorMain {

    public static void main(String[] args) {
        try {
            List<String> warnings = new ArrayList<String>();
            boolean overwrite = true;
            File configFile = new File(ClassLoader.getSystemResource("MGBConfig_portal.xml").getPath());
            File propertiesFile=new File(ClassLoader.getSystemResource("config_portal.properties").getPath());

//            File configFile = new File(ClassLoader.getSystemResource("MGBConfig_education.xml").getPath());
//            File propertiesFile=new File(ClassLoader.getSystemResource("config_education.properties").getPath());

//            File configFile = new File(ClassLoader.getSystemResource("MGBConfig_official.xml").getPath());
//            File propertiesFile=new File(ClassLoader.getSystemResource("config_official.properties").getPath());

//            File configFile = new File(ClassLoader.getSystemResource("MGBConfig_bonus.xml").getPath());
//            File propertiesFile=new File(ClassLoader.getSystemResource("config_bonus.properties").getPath());
            Properties properties=new Properties();
            properties.load(new FileInputStream(propertiesFile));
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            ServiceGenerator serviceGenerator=new ServiceGenerator(properties,config);
            myBatisGenerator.generate(serviceGenerator);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (XMLParserException e) {
            e.printStackTrace();
        }
    }
}
