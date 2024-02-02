package config;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class ConfigManager {

	private Class<?> vars;
	private File config_file;
	
	private String defaultFolderPath = "configs\\";
	
	public ConfigManager(Class<?> vars, String configFileName) throws Exception {
		this.config_file = new File(defaultFolderPath + configFileName + ".cfg");
		this.vars = vars;
	}
	
	public ConfigManager(Class<?> vars) {
		this.config_file = new File(defaultFolderPath + "default.cfg");
		this.vars = vars;
	}

	public void setSelectedConfig(String configFileName) {
		this.config_file = new File(defaultFolderPath + configFileName + ".cfg");
	}
	
	public boolean configFileExists() {
		return this.config_file.exists();
	}
	
	public void loadFileOld() throws Exception {
		for (Field field : vars.getFields()) {
			String val = getValueFromConfig(field.getName());
			
			if (field.getType() == int.class) {
				field.set(int.class, Integer.parseInt(val));
			}
			if (field.getType() == byte.class) {
				field.set(byte.class, Byte.parseByte(val));
			}
			if (field.getType() == long.class) {
				field.set(long.class, Long.parseLong(val));
			}
			if (field.getType() == double.class) {
				field.set(double.class, Double.parseDouble(val));
			}
			if (field.getType() == boolean.class) {
				field.set(boolean.class, Boolean.parseBoolean(val));
			}
			if (field.getType() == float.class) {
				field.set(float.class, Float.parseFloat(val));
			}
			if (field.getType() == short.class) {
				field.set(short.class, Short.parseShort(val));
			}
			if (field.getType() == char.class) {
				field.set(char.class, val.charAt(0));
			}
			if (field.getType() == java.lang.String.class) {
				field.set(java.lang.String.class, val);
			}
		}
	}

	public void loadFile() throws Exception {
	    Map<Class<?>, Function<String, ?>> parsers = new HashMap<>();
	    parsers.put(int.class, Integer::parseInt);
	    parsers.put(byte.class, Byte::parseByte);
	    parsers.put(long.class, Long::parseLong);
	    parsers.put(double.class, Double::parseDouble);
	    parsers.put(boolean.class, Boolean::parseBoolean);
	    parsers.put(float.class, Float::parseFloat);
	    parsers.put(short.class, Short::parseShort);
	    parsers.put(char.class, s -> s.charAt(0));
	    parsers.put(java.lang.String.class, s -> s);

	    for (Field field : vars.getFields()) {
	        String val = getValueFromConfig(field.getName());
	        Class<?> type = field.getType();
	        field.set(type, parsers.get(type).apply(val));
	    }
	}
	
	public void saveFile() throws Exception {
		if(!config_file.exists()) {
			String[] pathSplit = config_file.getPath().split("\\\\");
			String configName = pathSplit[pathSplit.length - 1];
			String path = config_file.getPath().substring(0, config_file.getPath().length() - configName.length());
			
			new File(path).mkdirs();
			config_file.createNewFile();
		}
		
		String configText = "";
		
		configText += "" +
				  "██╗██╗  ██╗     ██████╗ ██████╗ ███╗   ██╗███████╗██╗ ██████╗     ███╗   ███╗ █████╗ ███╗   ██╗ █████╗  ██████╗ ███████╗██████╗ \r\n"
				+ "██║╚██╗██╔╝    ██╔════╝██╔═══██╗████╗  ██║██╔════╝██║██╔════╝     ████╗ ████║██╔══██╗████╗  ██║██╔══██╗██╔════╝ ██╔════╝██╔══██╗\r\n"
				+ "██║ ╚███╔╝     ██║     ██║   ██║██╔██╗ ██║█████╗  ██║██║  ███╗    ██╔████╔██║███████║██╔██╗ ██║███████║██║  ███╗█████╗  ██████╔╝\r\n"
				+ "██║ ██╔██╗     ██║     ██║   ██║██║╚██╗██║██╔══╝  ██║██║   ██║    ██║╚██╔╝██║██╔══██║██║╚██╗██║██╔══██║██║   ██║██╔══╝  ██╔══██╗\r\n"
				+ "██║██╔╝ ██╗    ╚██████╗╚██████╔╝██║ ╚████║██║     ██║╚██████╔╝    ██║ ╚═╝ ██║██║  ██║██║ ╚████║██║  ██║╚██████╔╝███████╗██║  ██║\r\n"
				+ "╚═╝╚═╝  ╚═╝     ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚═╝     ╚═╝ ╚═════╝     ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝\r\n\n";
		
		ArrayList<String> categories = new ArrayList<String>();

		for (Field field : vars.getFields()) {
			if(field.isAnnotationPresent(ConfigVariable.class)) {
				String category = field.getAnnotation(ConfigVariable.class).category();
				
				if(!categories.contains(category)) {
					categories.add(category);
				}
			}
		}
		
		for (String category : categories) {
			configText += "[" + category + "]" + "\n";

			for (Field field : vars.getFields()) {
				if (field.getAnnotation(ConfigVariable.class).category().equals(category)) {
					configText += field.getName() + "=" + field.get(field) + "\n";
				}
			}

			configText += "\n";
		}

		FileWriter fw = new FileWriter(config_file.getPath());
		fw.write(configText);
		fw.close();
	}
	
	public String getValueFromConfig(String key) throws Exception {
		Scanner reader = new Scanner(config_file);
		while (reader.hasNextLine()) {
			String line = reader.nextLine();

			if (line.contains("=")) {
				String[] lineArr = line.split("=");

				if (lineArr[0].contains(key)) {
					return lineArr[1];
				}
			}
		}
		return "";
	}
}