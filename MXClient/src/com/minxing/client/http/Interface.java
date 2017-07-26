package com.minxing.client.http;

public enum Interface {
    UPGRADE("/api/v1/mobile/update_info", Object.class),									//系统升级检测
    PING("/api/v1/ping?a", Object.class);
    
	private String mInterface;
	private String formatFace;
	
	private Class<?> mClazz; 
	
	Interface(String aType,Class<?> clazz) { 
		mInterface = aType;
		formatFace = aType;
		mClazz = clazz;
	}
	public String getFormatFace(){
		return formatFace;
	}

	public String getInsType() {
		return mInterface;
	}
	
	public Class<?> getClazz(){
		return mClazz;
	}
	
	
	public Interface insertParam(Object... param) {
		if (param != null) {
			formatFace = String.format(mInterface, param);
		} else {
			formatFace = mInterface;
		}
		return this;
	}
	public void replaceInterface(String newInterface){
		mInterface = newInterface;
		formatFace = newInterface;
	}
	
}
