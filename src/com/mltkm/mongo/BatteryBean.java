package com.mltkm.mongo;

import java.util.ArrayList;
import java.util.Date;

import com.mongodb.DBObject;

public class BatteryBean {
	public long level = -1;
	public long date = -1;
	public ArrayList<String> errors = null;
	
	/* sample json document
	 * {
    "_id": {
        "$oid": "123442126eb93d27aade83f1"
    },
    "uploaderBattery": 87,
    "created_at": {
        "$date": "2014-10-07T01:29:54.534Z"
    }
	}
	 */
	BatteryBean() {
	}

	BatteryBean(DBObject doc) {
		setLevel(doc);
		setDate(doc);
	}

	public long getLevel() {
		return level;
	}

	public void setLevel(long level) {
		this.level = level;
	}

	public void setLevel(DBObject doc) {
		level = Util.getPositiveNumber(doc.get("uploaderBattery"));
		if(level == -2){
			addError("Could not parse battery level from: "+ doc.get("uploaderBattery").toString());
		}
	}
	
	public long getDate() {
		return date;
	}
	public Date getDateAsDate() {
		if(date < 0){
			return(null);
		}
		return(new Date(date));
	}
	public void setDate(long date) {
		this.date = date;
	}
	
	public void setDate(DBObject doc) {
		this.date  = Util.getDateAsMilliseconds(doc.get("created_at"));
		if(level == -2){
			addError("Could not parse date from: "+ doc.get("created_at").toString());
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
}
