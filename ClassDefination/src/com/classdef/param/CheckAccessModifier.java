package com.classdef.param;

import java.lang.reflect.Modifier;

public class CheckAccessModifier {
	public  String getAccessModifier(int modifier) {
		String accessModifier;
		if (Modifier.isAbstract(modifier))
			accessModifier = "Abstract";
		else if (Modifier.isFinal(modifier))
			accessModifier = "Final";
		else if (Modifier.isInterface(modifier))
			accessModifier = "Interface";
		else if (Modifier.isNative(modifier))
			accessModifier = "Native";
		else if (Modifier.isPrivate(modifier))
			accessModifier = "Private";
		else if (Modifier.isProtected(modifier))
			accessModifier = "Protected";
		else if (Modifier.isPublic(modifier))
			accessModifier = "Public";
		else if (Modifier.isStatic(modifier))
			accessModifier = "Static";
		else if (Modifier.isStrict(modifier))
			accessModifier = "Strict";
		else if (Modifier.isSynchronized(modifier))
			accessModifier = "Synchronized";
		else if (Modifier.isTransient(modifier))
			accessModifier = "Transient";
		else if (Modifier.isVolatile(modifier))
			accessModifier = "Volatile";
		else {
			accessModifier = "Program cannot find the Access Modifier!";
		}
		return accessModifier;
	}
}
