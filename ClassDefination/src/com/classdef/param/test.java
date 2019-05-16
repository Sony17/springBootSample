package com.classdef.param;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
public class test {
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Method[] methods = Employee.class.getDeclaredMethods();
		for (Method method : methods) {
			System.out.println(method.getName());
			System.out.println("-------------");
			Parameter[] parameters = method.getParameters();
			for (Parameter p : parameters) {
				if (p.isNamePresent()) {
					System.out.println(p.getName());
				}
			}
		}
	}
} 