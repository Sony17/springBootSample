package com.classdef.param;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Employee {
	@Id
	public int id;
	@Column
	public String name;
	public String designation;
	public int age;
	private float salary;

	public Employee(String name, String designation, int age, float salary) {
		this.name = name;
		this.designation = designation;
		this.age = age;
		this.salary = salary;

	}

	protected String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}
    @Version
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

}
