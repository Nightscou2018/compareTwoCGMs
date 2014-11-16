package com.mltkm.mongo;

import java.util.ArrayList;
import java.util.Date;

import com.mongodb.DBObject;

public class CGMBean {
	public static double BG_TOLLERANCE_PERCENTAGE = .10;
	public String device;
	public long date = -1;
	public String dateString;
	public long sgv = -1;
	public String direction;
	public long filtered;
	public long unfiltered;
	public long rssi = -1;
	public ArrayList<String> errors = null;

	/* sample json document
	 * beta "_id": { "$oid": "12344212dffaf0c7917c1def" }, "device": "dexcom",
	 * "date": 1412645227000, "dateString": "Mon Oct 06 21:27:07 EDT 2014",
	 * "sgv": 222, "direction": "FortyFiveUp", "filtered": 245504, "unfilterd":
	 * 246336, "rssi": 181
	 */

	public CGMBean() {

	}

	public CGMBean(DBObject doc) {
		setDevice(doc);
		setDate(doc);
		setDateString(doc);
		setSgv(doc);
		setDirection(doc);
		setFiltered(doc);
		setUnFiltered(doc);
		setRssi(doc);
	}

	public boolean isWithinTollerance(CGMBean bean){
		//is BG within a percentage of error
		double bgTolerence = sgv * BG_TOLLERANCE_PERCENTAGE;
		return(Math.abs(sgv - bean.getSgv()) < bgTolerence); 
	}
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
	
	public void setDevice(DBObject doc) {
		this.device = Util.getString(doc.get("device"));
	}
	
	public long getDate() {
		return date;
	}

	public Date getDateAsDate() {
		if(date < 0){
			return(null);
		}
		return (new Date(date));
	}

	public void setDate(long time) {
		this.date = time;
	}
	public void setDate(DBObject doc) {
		this.date = Util.getPositiveNumber(doc.get("date"));
		if(date == -2){
			addError("Could get date from: "+ doc.get("date"));
		}
	}
	 
	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	
	public void setDateString(DBObject doc) {
		this.dateString = Util.getString(doc.get("dateString"));
	}
	
	public long getSgv() {
		return sgv;
	}

	public void setSgv(long sgv) {
		this.sgv = sgv;
	}

	public void setSgv(DBObject doc) {
		sgv = Util.getPositiveNumber(doc.get("sgv"));
		if(sgv == -1){
			return;
		}
		if(sgv == -2){
			addError("Could not get sgv value from: "+ doc.get("sgv").toString());
		}else if (sgv < 20) {
			addErrorCode(sgv);
		}
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setDirection(DBObject doc) {
		this.direction = Util.getString(doc.get("direction"));
	}

	public long getFiltered() {
		return filtered;
	}

	public void setFiltered(long filtered) {
		this.filtered = filtered;
	}

	public void setFiltered(DBObject doc) {
		this.filtered = Util.getPositiveNumber(doc.get("filtered"));
		if(filtered == -2){
			addError("Could get filtered from: "+ doc.get("filtered").toString());
		}
	}

	public long getUnfiltered() {
		return unfiltered;
	}

	public void setUnfiltered(long unfiltered) {
		this.unfiltered = unfiltered;
	}

	public void setUnFiltered(DBObject doc) {
//TODO typo		this.unfiltered = Util.getPositiveNumber(doc.get("unfiltered"));
		this.unfiltered = Util.getPositiveNumber(doc.get("unfilterd"));
		if(unfiltered == -2){
			addError("Could get unfiltered from: "+ doc.get("unfiltered").toString());
		}
	}
	
	public long getRssi() {
		return rssi;
	}

	public void setRssi(long rssi) {
		this.rssi = rssi;
	}

	public void setRssi(DBObject doc) {
		this.rssi = Util.getPositiveNumber(doc.get("rssi"));
		if(rssi == -2){
			addError("Could get rssi from: "+ doc.get("rssi").toString());
		}
	}
	
	public ArrayList<String> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<String> errors) {
		this.errors = errors;
	}

	public void addError(String desc) {
		if (errors == null) {
			errors = new ArrayList<String>();
		}
		errors.add(desc);
	}

	public void addErrorCode(long errorCode) {
		addErrorCode((int) errorCode);
	}

	public void addErrorCode(int errorCode) {
		switch (errorCode) {
		case 1:
			addError("one");
			break;
		case 2:
			addError("two");
			break;
		default:
			addError("Unknow error code: " + errorCode);
			break;
		}
	}

}
