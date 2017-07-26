package com.minxing.client.upgrade;

import java.io.Serializable;

public class AppUpgradeInfo implements Serializable {

	private static final long serialVersionUID = 1040008906695519046L;

	private String app_name;
	private long size;
	private String fingerprint;
	private String version;
	private int version_code;
	private String description;
	private String upgrade_url;
	private String updateTime;
	private String smart_url;
	private long smart_size;
	private boolean isMandatoryUpgrade;
	private boolean isNeedUpgrade;
	private boolean isSmartUpgrade;

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getVersion_code() {
		return version_code;
	}

	public void setVersion_code(int version_code) {
		this.version_code = version_code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpgrade_url() {
		return upgrade_url;
	}

	public void setUpgrade_url(String upgrade_url) {
		this.upgrade_url = upgrade_url;
	}

	public boolean isNeedUpgrade() {
		return isNeedUpgrade;
	}

	public void setNeedUpgrade(boolean isNeedUpgrade) {
		this.isNeedUpgrade = isNeedUpgrade;
	}

	public boolean isMandatoryUpgrade() {
		return isMandatoryUpgrade;
	}

	public void setMandatoryUpgrade(boolean isMandatoryUpgrade) {
		this.isMandatoryUpgrade = isMandatoryUpgrade;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getSmart_url() {
		return smart_url;
	}

	public void setSmart_url(String smart_url) {
		this.smart_url = smart_url;
	}

	public long getSmart_size() {
		return smart_size;
	}

	public void setSmart_size(long smart_size) {
		this.smart_size = smart_size;
	}

	public boolean isSmartUpgrade() {
		return isSmartUpgrade;
	}

	public void setSmartUpgrade(boolean isSmartUpgrade) {
		this.isSmartUpgrade = isSmartUpgrade;
	}
}
