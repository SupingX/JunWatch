package com.mycj.junsda.bean;

public enum Unit {
	ZH("zh","kg", "cm"), EN("cn","lb", "lnch");
	private String weight;
	private String height;
	private String name;

	Unit(String name,String weight, String height) {
		this.name = name;
		this.weight = weight;
		this.height= height;
	};	
	
	public String getWeight(){
		return this.weight;
	}
	public String getHeight(){
		return this.height;
	}
	public String getName(){
		return this.name;
	}
}
