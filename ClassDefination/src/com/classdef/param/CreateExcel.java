package com.classdef.param;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class CreateExcel {
	static Logger LOGGER = Logger.getLogger(CreateExcel.class);

	public static void main(String[] args) {
		String log4jConfigFile = System.getProperty("user.dir") + File.separator + "log4j.properties";
		PropertyConfigurator.configure(log4jConfigFile);

		try (InputStream input = new FileInputStream("config.properties")) {

			Properties prop = new Properties();

			prop.load(input);
			LOGGER.debug("Package Folder :" + prop.getProperty("path"));
			String path = prop.getProperty("path");
			List<String> classList = getClass(path);
			LOGGER.info("Class List :" + classList);
			createExcel(classList, prop.getProperty("filename"), prop.getProperty("packageName"),
					prop.getProperty("formatTranspose"));
		} catch (IOException ex) {
			LOGGER.debug("Error while fething data from properties files");

		}

	}

	private static void createExcel(List<String> classList, String filename, String packageName, String format) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();

			for (String className : classList) {
				String classPath = packageName + "." + className;
				LOGGER.debug("ClassPath :" + classPath);

				Class<?> myObjectClass = Class.forName(classPath);
				HSSFSheet sheet = workbook.createSheet(className);
				Field[] fields = myObjectClass.getDeclaredFields();
				Method[] methods = myObjectClass.getDeclaredMethods();
				if (Boolean.valueOf(format))
					getFormatTranspose(fields, methods, sheet);
				else
					getFormat(fields, methods, sheet);

			}

			FileOutputStream fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);

			fileOut.close();
			LOGGER.debug("Your excel file has been generated!");

		} catch (Exception ex) {

			LOGGER.debug("Exception Creating Workbook");
			ex.printStackTrace();

		}
	}

	private static List<String> getClass(String path) {
		List<String> classList = new ArrayList<String>();

		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				LOGGER.info("Class Name :" + child.getName());
				String className = child.getName().split("\\.")[0];

				LOGGER.info("Class Name :" + className);
				classList.add(className);
			}
		} else {
			LOGGER.debug("No class found");

		}
		LOGGER.debug("Class List : " + classList);
		return classList;
	}

	private static void getFormatTranspose(Field[] fields, Method[] methods, HSSFSheet sheet) {
		CheckAccessModifier accessModifier = new CheckAccessModifier();
		HSSFRow rowhead = sheet.createRow((short) 0);
		rowhead.createCell(0).setCellValue("Attribute.");
		rowhead.createCell(2).setCellValue("Modifier");
		rowhead.createCell(1).setCellValue("Type ");
		rowhead.createCell(3).setCellValue("Description ");
		rowhead.createCell(4).setCellValue("Constraints ");
		rowhead.createCell(5).setCellValue("Annotation ");

		int rowId = 1;
		for (Field f : fields) {
			f.setAccessible(true);

			String attributeType;
			if (f.getGenericType().toString().contains(("."))) {
				String[] fn = f.getGenericType().toString().split("\\.");
				attributeType = fn[fn.length - 1];
			} else {
				attributeType = f.getGenericType().toString();

			}
			StringBuilder sb = new StringBuilder();
			Annotation[] fieldAnnotations = f.getDeclaredAnnotations();
			for (Annotation a : fieldAnnotations) {
				sb = sb.append(a).append("/");
			}
			LOGGER.debug("Field Name :" + f.getName().toString() + "Field Modifier :"
					+ accessModifier.getAccessModifier(f.getModifiers()) + "Field Type : " + attributeType);

			HSSFRow row = sheet.createRow(rowId++);
			row.createCell(0).setCellValue(f.getName().toString());
			row.createCell(2).setCellValue(accessModifier.getAccessModifier(f.getModifiers()));
			row.createCell(1).setCellValue(attributeType);
			row.createCell(3).setCellValue("");
			row.createCell(4).setCellValue("");
			row.createCell(5).setCellValue(sb.toString());
		}
		HSSFRow rowhead2 = sheet.createRow(rowId + 2);
		rowhead2.createCell(0).setCellValue("Method Name");
		rowhead2.createCell(2).setCellValue("Return type");
		rowhead2.createCell(1).setCellValue("Modifier ");
		rowhead2.createCell(3).setCellValue("Input Parameters ");
		rowhead2.createCell(4).setCellValue("Output");
		rowhead2.createCell(5).setCellValue("Pre-Conditions");
		rowhead2.createCell(6).setCellValue("Post-Conditions");
		rowhead2.createCell(7).setCellValue("Method called by this method ");
		rowhead2.createCell(8).setCellValue("Legacy Method Mapping");
		rowhead2.createCell(9).setCellValue("Annotation ");

		rowId = rowId + 3;

		for (Method m : methods) {
			m.setAccessible(true);

			String parameType = "";
			StringBuilder parameters = new StringBuilder();

			Type[] parameterTypes = m.getParameterTypes();
			for (Type parameterType : parameterTypes) {
				if (parameterType.getTypeName().contains(("."))) {
					String[] fn = parameterType.getTypeName().split("\\.");
					parameType = fn[fn.length - 1];
				} else {
					parameType = parameterType.getTypeName();

				}

				parameters = parameters.append(parameType).append("/");
			}

			Annotation[] methodAnnotations = m.getDeclaredAnnotations();
			StringBuilder sb = new StringBuilder();

			for (Annotation a : methodAnnotations) {
				sb = sb.append(a).append("/");
			}
			String returnType;
			if (m.getReturnType().toString().contains(("."))) {
				String[] fn = m.getReturnType().toString().split("\\.");
				returnType = fn[fn.length - 1];
			} else {
				returnType = m.getReturnType().toString();

			}
			LOGGER.debug(("Method Name : " + m.getName().toString() + "Method Parameters : " + parameters
					+ "Methods ReturnType :" + returnType) + "Access Modifier : "
					+ accessModifier.getAccessModifier(m.getModifiers()));

			HSSFRow row = sheet.createRow(rowId++);
			row.createCell(0).setCellValue(m.getName().toString());
			row.createCell(1).setCellValue(returnType);
			row.createCell(2).setCellValue(accessModifier.getAccessModifier(m.getModifiers()));
			row.createCell(3).setCellValue(parameters.toString());
			row.createCell(4).setCellValue(returnType);
			row.createCell(5).setCellValue("");
			row.createCell(6).setCellValue("");
			row.createCell(7).setCellValue("");
			row.createCell(8).setCellValue("");
			row.createCell(9).setCellValue(sb.toString());

		}

	}

	private static void getFormat(Field[] fields, Method[] methods, HSSFSheet sheet) {
		CheckAccessModifier accessModifier = new CheckAccessModifier();

		int rowId = 0;
		for (Field f : fields) {

			f.setAccessible(true);
			String attributeType;
			if (f.getGenericType().toString().contains(("."))) {
				String[] fn = f.getGenericType().toString().split("\\.");
				attributeType = fn[fn.length - 1];
			} else {
				attributeType = f.getGenericType().toString();

			}

			StringBuilder sb = new StringBuilder();
			Annotation[] fieldAnnotations = f.getDeclaredAnnotations();
			for (Annotation a : fieldAnnotations) {
				sb = sb.append(a).append("/");
			}
			LOGGER.debug(("Method Name : " + f.getName().toString() + "Attribute Type : " + attributeType
					+ "Access Modifier : " + accessModifier.getAccessModifier(f.getModifiers())));

			HSSFRow row1 = sheet.createRow(rowId++);
			row1.createCell(0).setCellValue("Attributes");
			row1.createCell(1).setCellValue(f.getName());
			HSSFRow row2 = sheet.createRow((rowId++));
			row2.createCell(0).setCellValue("Type");
			row2.createCell(1).setCellValue(attributeType);

			HSSFRow row3 = sheet.createRow(rowId++);
			row3.createCell(0).setCellValue("Modifier");
			row3.createCell(1).setCellValue(accessModifier.getAccessModifier(f.getModifiers()));
			HSSFRow row4 = sheet.createRow(rowId++);
			row4.createCell(0).setCellValue("Description");
			row4.createCell(1).setCellValue(" ");
			HSSFRow row5 = sheet.createRow(rowId++);
			row5.createCell(0).setCellValue("Constraints");
			row5.createCell(1).setCellValue(" ");

			HSSFRow row6 = sheet.createRow(rowId++);
			row6.createCell(0).setCellValue("Annotations");
			row6.createCell(1).setCellValue(sb.toString());
			rowId++;

		}
		rowId = rowId + 3;

		for (Method m : methods) {
			m.setAccessible(true);
			Annotation[] methodAnnotations = m.getDeclaredAnnotations();
			StringBuilder sb = new StringBuilder();
			StringBuilder parameters = new StringBuilder();

			for (Annotation a : methodAnnotations) {
				sb = sb.append(a).append("/");
			}
			String parameType = "";

			m.getParameterTypes();
			Type[] parameterTypes = m.getParameterTypes();
			for (Type parameterType : parameterTypes) {
				if (parameterType.getTypeName().contains(("."))) {
					String[] fn = parameterType.getTypeName().split("\\.");
					parameType = fn[fn.length - 1];
				} else {
					parameType = parameterType.getTypeName();

				}
				parameters = parameters.append(parameType).append("/");
			}

			HSSFRow row1 = sheet.createRow(rowId++);
			row1.createCell(0).setCellValue("Method Name");
			row1.createCell(1).setCellValue(m.getName().toString());

			HSSFRow row2 = sheet.createRow((rowId++));
			row2.createCell(0).setCellValue("Return Type");
			String returnType;
			if (m.getReturnType().toString().contains(("."))) {
				String[] fn = m.getReturnType().toString().split("\\.");
				returnType = fn[fn.length - 1];
			} else {
				returnType = m.getReturnType().toString();

			}
			LOGGER.debug(("Method Name : " + m.getName().toString() + "Method Parameters : " + parameters
					+ "Methods ReturnType :" + returnType) + "Access Modifier : "
					+ accessModifier.getAccessModifier(m.getModifiers()));

			row2.createCell(1).setCellValue(returnType);

			HSSFRow row3 = sheet.createRow(rowId++);
			row3.createCell(0).setCellValue("Modifier");
			row3.createCell(1).setCellValue(accessModifier.getAccessModifier(m.getModifiers()));
			HSSFRow row4 = sheet.createRow(rowId++);
			row4.createCell(0).setCellValue("Input Parameters");
			row4.createCell(1).setCellValue(parameters.toString());

			HSSFRow row5 = sheet.createRow(rowId++);
			row5.createCell(0).setCellValue("Output");
			row5.createCell(1).setCellValue(returnType);

			HSSFRow row6 = sheet.createRow(rowId++);
			row6.createCell(0).setCellValue("Pre-Conditions");
			row6.createCell(1).setCellValue("");

			HSSFRow row7 = sheet.createRow(rowId++);
			row7.createCell(0).setCellValue("Post-Conditions");
			row7.createCell(1).setCellValue("");

			HSSFRow row8 = sheet.createRow(rowId++);
			row8.createCell(0).setCellValue("Method called bt this method");
			row8.createCell(1).setCellValue("");

			HSSFRow row9 = sheet.createRow(rowId++);
			row9.createCell(0).setCellValue("Legacy Method Mapping");
			row9.createCell(1).setCellValue("");

			HSSFRow row10 = sheet.createRow(rowId++);
			row10.createCell(0).setCellValue("Annotations");
			row10.createCell(1).setCellValue(sb.toString());

			rowId++;

		}
	}
}