package com.schutzstaffel.fmplayer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.schutzstaffel.fmplayer.MainActivity;
import com.schutzstaffel.fmplayer.R;

public class PlayActions {

	public static MediaPlayer player = new MediaPlayer();

	private static List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
	private static boolean isIntl = false;
	private static int position = 0;
	private static Context mContext;

	private static Animation anim_start, anim_end;

	private static boolean isAnim = false;

	/**
	 * Get channel list from server
	 */
	public static void getChannelList() {
		StringRequest mRequest = new StringRequest(ClientAPIs.API_CHANNELS_URL,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject channels = new JSONObject(response);
							JSONArray channelsArr = channels
									.getJSONArray("channels");
							for (int i = 0; i < channelsArr.length(); i++) {
								JSONObject obj = (JSONObject) channelsArr
										.get(i);
								Log.i(ClientAPIs.TAG,
										"Channel_name:" + obj.getString("name"));
								Log.i(ClientAPIs.TAG,
										"Channel_id:"
												+ obj.getString("channel_id"));
							}
						} catch (JSONException e) {
							Log.e(ClientAPIs.TAG, "Error on JsonObject");
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(ClientAPIs.TAG, "Err on getting channels:"
								+ error.getMessage());
					}
				});
		MainActivity.mQueue.add(mRequest);
	}

	public static void getSongList(final int channel, final String type) {
		final HashMap<String, String> mHashMap = new HashMap<String, String>();

		if (UserActions.isLogin) {
			mHashMap.put(ClientAPIs.API_USER_ID, ClientAPIs.getid());
			mHashMap.put(ClientAPIs.API_TOKEN, ClientAPIs.gettoken());
			mHashMap.put(ClientAPIs.API_EXPIRE, ClientAPIs.getexpire());
		}
		StringRequest mRequest = new StringRequest(Method.POST,
				ClientAPIs.API_SONGS_URL, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONArray arr = new JSONObject(response)
									.getJSONArray("song");
							// Log.i(ClientAPIs.TAG, response);
							Log.i(ClientAPIs.TAG, "Type:" + type);
							int num = 1;

							for (int i = 0; i < arr.length(); i++) {
								JSONObject obj = (JSONObject) arr.get(i);
								HashMap<String, String> data = new HashMap<String, String>();
								data.put("url",
										obj.getString("url").replace("\\", ""));
								data.put("ssid", obj.getString("ssid"));
								data.put("sid", obj.getString("sid"));
								data.put("title", obj.getString("title"));
								data.put("artist", obj.getString("artist"));
								data.put("albumtitle",
										obj.getString("albumtitle"));
								data.put("picture", obj.getString("picture")
										.replace("\\", ""));
								results.add(data);
								num++;
							}
							MainActivity.btn_play.setEnabled(true);
							MainActivity.btn_pause.setEnabled(true);
							MainActivity.btn_next.setEnabled(true);
							if (channel == 14) {
								Toast.makeText(mContext, "已切换至电子", 2000).show();
							}
							if (channel == 188) {
								Toast.makeText(mContext, "已切换至布鲁斯", 2000)
										.show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(ClientAPIs.TAG,
								"Get song list err:" + error.getMessage());
						if (MainActivity.dialog.isShowing()) {
							MainActivity.dialog.dismiss();
							Toast.makeText(mContext, "网络抽风", Toast.LENGTH_LONG)
									.show();
						}
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				mHashMap.put(ClientAPIs.API_APP_NAME, ClientAPIs.APP_NAME_VALUE);
				mHashMap.put(ClientAPIs.API_VERSION, ClientAPIs.VERSION_VALUE);
				mHashMap.put(ClientAPIs.API_CHANNEL,
						String.valueOf(MainActivity.channel));
				mHashMap.put("type", type);
				if (type == "p") {
					mHashMap.put("sid", results.get(position).get("sid"));
				}
				return mHashMap;
			}
		};
		MainActivity.mQueue.add(mRequest);
	}

	public static void play() {
		if (isIntl) {
			try {
				if (!player.isPlaying()) {
					player.reset();
					player.setDataSource(results.get(position).get("url"));
					setInfos();
					MainActivity.img_album.startAnimation(anim_start);
					player.prepareAsync();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void pause() {
		if (player.isPlaying()) {
			player.pause();
		} else {
			player.start();
		}
	}

	public static void next() {
		if (isIntl) {
			MainActivity.img_album.startAnimation(anim_end);
			if ((position + 1) == (results.size() - 2)) {
				Log.i(ClientAPIs.TAG, "Getting list from server");
				getSongList(MainActivity.channel, "p");
			}
			if (results.size() == 0) {
				getSongList(MainActivity.channel, "n");
			}
			if (results.size() >= 30) {
				results.clear();
				position = 0;
			}
			try {
				position++;
				player.reset();
				player.setDataSource(results.get(position).get("url"));
				setInfos();
				MainActivity.img_album.startAnimation(anim_start);
				player.prepareAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void setInfos() {
		ImageRequest mImgRequest = new ImageRequest(results.get(position).get(
				"picture"), new Response.Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap response) {
				MainActivity.img_album.setImageBitmap(response);
			}
		}, 0, 0, Config.RGB_565, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				MainActivity.img_album.setImageResource(R.drawable.ic_launcher);
			}
		});
		MainActivity.mQueue.add(mImgRequest);
		String title, artist, albumTitle;
		title = results.get(position).get("title");
		artist = results.get(position).get("artist");
		albumTitle = results.get(position).get("albumtitle");
		MainActivity.text_title.setText(title + " - " + artist);
		MainActivity.text_albutm_title.setText(albumTitle);
	}

	public static void intl(Context context) {
		mContext = context;
		player.setAudioStreamType(2);
		getSongList(MainActivity.channel, "n");
		player.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				player.start();
				Log.i(ClientAPIs.TAG, "Now playing:"
						+ results.get(position).get("title"));
				Log.i(ClientAPIs.TAG, "Position:" + (position + 1));
				Log.i(ClientAPIs.TAG, "List size:" + results.size());
			}
		});
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				MainActivity.img_album.startAnimation(AnimationUtils
						.makeOutAnimation(mContext, true));
				next();
			}
		});

		anim_start = AnimationUtils.loadAnimation(mContext, R.anim.ani_fade_in);
		anim_end = AnimationUtils.loadAnimation(mContext, R.anim.ani_fade_out);

		anim_start.setAnimationListener(animL_start);
		anim_end.setAnimationListener(animL_end);

		isIntl = true;
	}

	public static int getListSize() {
		return results.size();
	}

	public static void clearSongList() {
		results.clear();
		position = 0;
	}

	private static AnimationListener animL_end = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			isAnim = true;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			isAnim = false;
			MainActivity.img_album.setVisibility(View.INVISIBLE);
		}
	};

	private static AnimationListener animL_start = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			isAnim = true;
			MainActivity.img_album.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			isAnim = false;
		}
	};

}
