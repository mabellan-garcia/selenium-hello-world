package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	
	public static Properties config = new Properties();
    public static FileInputStream fis;  
    
    Configuration(){
    	try {
			fis = new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/config.properties");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			config.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public String getProperty(String property) {
		return config.getProperty(property);
	}
}
