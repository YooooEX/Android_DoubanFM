package com.schutzstaffel.fmplayer.utils;

public class ClientAPIs {
	public static final String TAG = "APP";

	public static final String API_LOGIN_URL = "http://www.douban.com/j/app/login";
	public static final String URL_HEADER = "Content-Type: application/x-www-form-urlencoded";

	public static final String API_CHANNELS_URL = "http://www.douban.com/j/app/radio/channels";
	public static final String API_CHANNEL = "channel";

	public static final String API_SONGS_URL = "http://www.douban.com/j/app/radio/people";

	public static final String API_LYRICS_URL = "http://api.douban.com/v2/fm/lyric";

	public static final String API_APP_NAME = "app_name";
	public static final String APP_NAME_VALUE = "radio_desktop_win";

	public static final String API_VERSION = "version";
	public static final String VERSION_VALUE = "100";

	public static final String API_USER_ID = "user_id";
	private static String user_id;

	public static final String API_USERNAME = "user_name";
	private static String user_name;

	public static final String API_TOKEN = "token";
	private static String user_token;

	public static final String API_EXPIRE = "expire";
	private static String user_expire;

	public static String getid() {
		return user_id;
	}

	public static void setid(String id) {
		user_id = id;
	}

	public static String getname() {
		return user_name;
	}

	public static void setname(String name) {
		user_name = name;
	}

	public static String gettoken() {
		return user_token;
	}

	public static void settoken(String token) {
		user_token = token;
	}

	public static String getexpire() {
		return user_expire;
	}

	public static void setexpire(String expire) {
		user_expire = expire;
	}

}
