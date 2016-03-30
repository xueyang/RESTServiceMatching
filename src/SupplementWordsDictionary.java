import java.util.HashMap;
import java.util.Map;


public class SupplementWordsDictionary {
	Map<String, String> supplementWordsForCommon = new HashMap<String, String>();
	Map<String, String> supplementWordsForTwitter = new HashMap<String, String>();
	Map<String, String> supplementWordsForDouban = new HashMap<String, String>();
	Map<String, String> supplementWordsForRenren = new HashMap<String, String>();
	Map<String, String> supplementWordsForWeibo = new HashMap<String, String>();
	Map<String, String> supplementWordsForFlickr = new HashMap<String, String>();

	
	public SupplementWordsDictionary()
	{
		supplementWordsForCommon.put("geo", "geography");
		supplementWordsForCommon.put("lat", "latitude");
		supplementWordsForCommon.put("lon", "longitude");
		supplementWordsForCommon.put("long", "longitude");
		supplementWordsForCommon.put("desc", "descending");
		supplementWordsForCommon.put("q", "query");
		supplementWordsForCommon.put("checkin", "check in");
		supplementWordsForCommon.put("app", "application");
		supplementWordsForCommon.put("uid", "userId");
		supplementWordsForCommon.put("meta", "key word");
		
		supplementWordsForTwitter.put("tweet", "blog");
		supplementWordsForTwitter.put("rts", "send on");
		supplementWordsForTwitter.put("retweet", "send on");
		
		supplementWordsForDouban.put("pid", "photoId");
		supplementWordsForDouban.put("shuo", "broadcast");
		supplementWordsForDouban.put("rec", "recommmend");
		supplementWordsForDouban.put("wisher", "wish");
		
		supplementWordsForRenren.put("ugc", "UserGeneratedContent");
		
		supplementWordsForWeibo.put("pic", "picture");
		supplementWordsForWeibo.put("poi", "pointOfInterest");
		supplementWordsForWeibo.put("pois", "pointOfInterest");
		supplementWordsForWeibo.put("rip", "realInternetProtocolAddress");
		supplementWordsForWeibo.put("starttime", "startTime");
		supplementWordsForWeibo.put("endtime", "endTime");
		supplementWordsForWeibo.put("cid", "commendId");
		supplementWordsForWeibo.put("ori", "origin");
		supplementWordsForWeibo.put("poiid", "pointOfInterestId");
		supplementWordsForWeibo.put("keyword", "key word");
		supplementWordsForWeibo.put("repost", "send on");
		
		supplementWordsForFlickr.put("bbox", "boundingBox");
		supplementWordsForFlickr.put("woe_id", "whereOnEarthId");
		supplementWordsForFlickr.put("fave", "favorite");
		supplementWordsForFlickr.put("photoset", "photoSet");
		supplementWordsForFlickr.put("Photoset", "PhotoSet");
		supplementWordsForFlickr.put("Photostream", "PhotoStream");
		
		
	}
}
