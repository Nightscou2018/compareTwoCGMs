package com.mltkm.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class CompareTwoCGMs {
	private static String BETA_URI = null;
	private static String PROD_URI = null;
	private static String BETA_CGM_COLLECTION = null;
	private static String PROD_CGM_COLLECTION = null;
	private static String BETA_DEVICE_COLLECTION = null;
	private static String PROD_DEVICE_COLLECTION = null;
	
	public static ArrayList<CGMBean> getCGMBeans(DBCollection collection, Date beginTime, Date endTime)
	{
		ArrayList<CGMBean> beans = new ArrayList<CGMBean>();
		
		System.out.println(beginTime +" <= "+ endTime);
        BasicDBObject findQuery = new BasicDBObject("date", new BasicDBObject("$gte", beginTime.getTime()).append("$lte", endTime.getTime()));
        BasicDBObject orderBy = new BasicDBObject("date", 1);
        
		//get CGM info
        DBCursor docs = collection.find(findQuery).sort(orderBy);
        
        while(docs.hasNext()){
            DBObject doc = docs.next();
            CGMBean bean = new CGMBean(doc);
            beans.add(bean);
        }
        return(beans);
	}
	
	public static ArrayList<BatteryBean> getBatteryBeans(DBCollection collection, Date beginTime, Date endTime)
	{
		ArrayList<BatteryBean> beans = new ArrayList<BatteryBean>();
        BasicDBObject findQuery = new BasicDBObject("created_at", new BasicDBObject("$gte", beginTime).append("$lte", endTime));
        BasicDBObject orderBy = new BasicDBObject("created_at", 1);
        
		//get battery info
        DBCursor docs = collection.find(findQuery).sort(orderBy);
        while(docs.hasNext()){
            DBObject doc = docs.next();
//            System.out.println("doc: "+ doc.get("created_at") + ", " + doc.get("uploaderBattery"));
            BatteryBean bean = new BatteryBean(doc);
            beans.add(bean);
        }
        return(beans);
	}
	public static void init(String filename){
		Properties mongoProp = Util.loadProperties(filename);
		PROD_URI = mongoProp.getProperty("PROD_URI");
		PROD_CGM_COLLECTION = mongoProp.getProperty("PROD_CGM_COLLECTION");
		PROD_DEVICE_COLLECTION = mongoProp.getProperty("PROD_DEVICE_COLLECTION");
		
		BETA_URI = mongoProp.getProperty("BETA_URI");
		BETA_CGM_COLLECTION = mongoProp.getProperty("BETA_CGM_COLLECTION");
		BETA_DEVICE_COLLECTION = mongoProp.getProperty("BETA_DEVICE_COLLECTION");
		
//		System.out.println(PROD_URI +"\n"+ PROD_CGM_COLLECTION +"\n"+ PROD_DEVICE_COLLECTION);
//		System.out.println(BETA_URI +"\n"+ BETA_CGM_COLLECTION +"\n"+ BETA_DEVICE_COLLECTION);
	}
	 public static void main(String[] args) throws UnknownHostException
	 {
		init(args[0]);
		int hoursToCompare = Integer.parseInt(args[1]);
	    ArrayList<CGMBean> betaCGMList = null;
	    ArrayList<CGMBean> prodCGMList = null;
	    
        MongoClientURI betaURI  = new MongoClientURI(BETA_URI); 
        MongoClient betaClient = new MongoClient(betaURI);
        DB betaDB = betaClient.getDB(betaURI.getDatabase());
        
        MongoClientURI prodURI  = new MongoClientURI(PROD_URI);
        MongoClient prodClient = new MongoClient(prodURI);
        DB prodDB = prodClient.getDB(prodURI.getDatabase());

        DBCollection betaCGMCollection = betaDB.getCollection(BETA_CGM_COLLECTION);
        DBCollection prodCGMCollection = prodDB.getCollection(PROD_CGM_COLLECTION);
        
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.HOUR, -1 * hoursToCompare);
        
        betaCGMList = getCGMBeans(betaCGMCollection, startTime.getTime(), Calendar.getInstance().getTime());
	    prodCGMList = getCGMBeans(prodCGMCollection, startTime.getTime(), Calendar.getInstance().getTime());
	     
	    betaClient.close();
	    prodClient.close();
	       	    
        
        int betaIndex = 0;
        int prodIndex = 0;
        boolean betaDone = false;
        boolean prodDone = false;
        
        //process CGM data
	     System.out.println("beta collection size= "+ betaCGMList.size() +" prod collection size= "+ prodCGMList.size());
	     betaIndex = 0;
	     prodIndex = 0;
	     betaDone = false;
	     prodDone = false;
	     while((betaIndex < betaCGMList.size() && prodIndex < prodCGMList.size()) && (!betaDone && !prodDone)){
	        	//figure out which index(s) need to be incremented
	        	CGMBean betaBean = betaCGMList.get(betaIndex);
	        	CGMBean prodBean = prodCGMList.get(prodIndex);
	        	boolean isTimeCloseEnough = Util.isDateWithRange(betaBean.getDate(), prodBean.getDate());
	        	
	        	String accurate = "n/a";
	        	String direction = "n/a";
	        	boolean hasAllData = (betaBean.getFiltered()>0 && betaBean.getUnfiltered() >0 && betaBean.getUnfiltered()>0);
	        	String data = betaBean.getFiltered() +"," + betaBean.getUnfiltered() +", "+ betaBean.getRssi(); 
	        	if(isTimeCloseEnough){
	        		accurate = (betaBean.isWithinTollerance(prodBean)) ? "+++" : "---";
	        		direction = (betaBean.getDirection().equalsIgnoreCase(prodBean.getDirection())) ? betaBean.getDirection() : betaBean.getDirection() +" != "+ prodBean.getDirection();
	        	}
//	        	System.out.println(accurate +" "+ direction +" betaCGM["+ betaIndex +"]="+ betaBean.getSgv() +" prodCGM["+ prodIndex +"]="+ prodBean.getSgv() +" b:"+ betaBean.getDateAsDate() +" p:"+ prodBean.getDateAsDate());
	        	System.out.println(accurate +" "+ direction +" betaCGM["+ betaIndex +"]="+ betaBean.getSgv() +" prodCGM["+ prodIndex +"]="+ prodBean.getSgv() +" b:"+ betaBean.getDateAsDate() +" p:"+ prodBean.getDateAsDate());
	        	
	        	if(isTimeCloseEnough){
	        		betaIndex++;
	        		prodIndex++;
	        	}else if((betaDone && prodBean.date > betaBean.date) || prodDone){
	        		betaIndex++;
	        	}else{
	        		prodIndex++;
	        	}

	        	if(betaIndex >= betaCGMList.size()){
	        		betaIndex = betaCGMList.size() -1;
	        		betaDone = true;
	        	}
	        	if(prodIndex >= prodCGMList.size()){
	        		prodIndex = prodCGMList.size() -1;
	        		prodDone = true;
	        	}
	        	
	        	betaBean = betaCGMList.get(betaIndex);
	        	prodBean = prodCGMList.get(prodIndex);
	        	
	        }
        
        boolean hasGaps = false;
        for(int idx=0; idx< betaCGMList.size()-1; idx++){
        	CGMBean curr = betaCGMList.get(idx);
        	CGMBean next = betaCGMList.get(idx+1);
        	if(Util.hasGap(curr.getDate(), next.getDate())){
        		hasGaps = true;
        		System.out.println("gap at "+idx +": "+ curr.getDateString() +" "+ next.getDateString());
        	}
        }
//        System.out.println("contains at least one gap: "+ hasGaps);
    }
}
