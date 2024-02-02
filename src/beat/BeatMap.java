package beat;

import org.json.JSONObject;

public class BeatMap {
	
	/* default object */
	private int beatmap_id;
	private int count;
	
	/* beatmap object */
	private int beatmapset_id;
	private int difficulty_rating;
	private String mode;
	private String status;
	private int total_length;
	private int user_id;
	private String version;
	
	/* beatmapset object */
	private String artist;
	private String creator;
	private int favourite_count;
	private int total_play_count;
	private String title;
	
	public BeatMap(JSONObject beatObj) {
		JSONObject beatmapObj = beatObj.getJSONObject("beatmap");
		JSONObject beatmapsetObj = beatObj.getJSONObject("beatmapset");
		
		/* default object */
		beatmap_id = beatObj.getInt("beatmap_id");
		count = beatObj.getInt("count");
		
		/* beatmap object */
		beatmapset_id = beatmapObj.getInt("beatmapset_id");
		difficulty_rating = beatmapObj.getInt("difficulty_rating");
		mode = beatmapObj.getString("mode");
		status = beatmapObj.getString("status");
		total_length = beatmapObj.getInt("total_length");
		user_id = beatmapObj.getInt("user_id");
		version = beatmapObj.getString("version");
		
		/* beatmapset object */
		artist = beatmapsetObj.getString("artist");
		creator = beatmapsetObj.getString("creator");
		favourite_count = beatmapsetObj.getInt("favourite_count");
		total_play_count = beatmapsetObj.getInt("play_count");
		title = beatmapsetObj.getString("title");
	}

	public int getBeatmap_id() {
		return beatmap_id;
	}

	public void setBeatmap_id(int beatmap_id) {
		this.beatmap_id = beatmap_id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getBeatmapset_id() {
		return beatmapset_id;
	}

	public void setBeatmapset_id(int beatmapset_id) {
		this.beatmapset_id = beatmapset_id;
	}

	public int getDifficulty_rating() {
		return difficulty_rating;
	}

	public void setDifficulty_rating(int difficulty_rating) {
		this.difficulty_rating = difficulty_rating;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTotal_length() {
		return total_length;
	}

	public void setTotal_length(int total_length) {
		this.total_length = total_length;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public int getFavourite_count() {
		return favourite_count;
	}

	public void setFavourite_count(int favourite_count) {
		this.favourite_count = favourite_count;
	}

	public int getTotal_play_count() {
		return total_play_count;
	}

	public void setTotal_play_count(int total_play_count) {
		this.total_play_count = total_play_count;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
