package beat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import config.GlobalVariables;


public class BeatUtils {

	public static ArrayList<String> getIDsFromLocation(String location) {
		ArrayList<String> IDs = new ArrayList<String>();
		
		File directory = new File(location);
		File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".osz"));

		for (File file : files) {
			String digits = file.getName().replaceAll("[^\\d]", "");
		    IDs.add(digits);
		}
		
		return IDs;
	}
	
	public static ArrayList<BeatMap> removeFromList(ArrayList<BeatMap> beats, ArrayList<String> IDs) {
		ArrayList<BeatMap> newBeats = new ArrayList<BeatMap>();
		
		for(BeatMap beat : beats) {
			boolean shouldAdd = true;
			
			for(String ID : IDs) {
				if((beat.getBeatmapset_id() + "").equals(ID)) {
					shouldAdd = false;
				}
			}
			
			if(shouldAdd) {
				newBeats.add(beat);
			}
		}
		
		return newBeats;
	}
	
	public static ArrayList<BeatMap> getUserBeatMaps(String userID) {
		int index = 0;

		ArrayList<BeatMap> beats = new ArrayList<BeatMap>();

		try {
			for (;;) {
				int offset = index * 100;

				ArrayList<BeatMap> newBeats = getUserBeatmapsUsingOffset(userID, 100, offset);
				if (newBeats.size() == 0) {
					break;
				}

				beats.addAll(newBeats);
				index++;

				Thread.sleep(GlobalVariables.BeatmapScanDelayMS);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error occurred. Please check your UserID.");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occurred. Likely rate limited.");
			System.exit(0);
		}

		return beats;
	}

	public static JSONArray getArrayFromURI(String sURI) throws Exception {
		URI uri = new URI(sURI);
		JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
		JSONArray root = new JSONArray(tokener);

		return root;
	}

	public static void downloadMap(int beatmapset_id, String cookie) {
		File dlFile = new File("downloads//");
		dlFile.mkdir();

		try {
			File file = new File("downloads\\" + beatmapset_id + ".osz");
			FileOutputStream fout = new FileOutputStream(file);

			URL url = new URL("https://osu.ppy.sh/beatmapsets/" + beatmapset_id + "/download");
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("referer", "https://osu.ppy.sh/beatmapsets/" + beatmapset_id);
			urlConnection.setRequestProperty("cookie", cookie);
			IOUtils.copy(urlConnection.getInputStream(), fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not download map: " + beatmapset_id);
		}
	}

	public static void downloadMapExternal(int beatmapset_id) throws Exception {
		File dlFile = new File("downloads//");
		dlFile.mkdir();

		File file = new File("downloads\\" + beatmapset_id + ".osz");
		FileOutputStream fout = new FileOutputStream(file);
		URL url = new URL("https://proxy.nerinyan.moe/d/" + beatmapset_id);
		
		try {
			URLConnection urlConnection = url.openConnection();
			IOUtils.copy(urlConnection.getInputStream(), fout);
			fout.close();
		} catch (Exception e) {
			fout.close();
			file.delete();
			FileWriter fw = new FileWriter("errors.txt", true);
			String lineToAppend = "\r\nhttps://osu.ppy.sh/beatmapsets/" + beatmapset_id;
			fw.write(lineToAppend);
			fw.close();
		}
	}

	public static ArrayList<BeatMap> getBeatsWithoutDuplicates(final List<BeatMap> beats) {
		Set<BeatMap> beatSet = new TreeSet<BeatMap>(new BeatComparator());
		for (BeatMap beat : beats) {
			beatSet.add(beat);
		}
		ArrayList<BeatMap> withoutDuplicates = new ArrayList<BeatMap>(beatSet);
		return reverseArrayList(withoutDuplicates);
	}

	public static ArrayList<BeatMap> reverseArrayList(List<BeatMap> withoutDuplicates) {
		ArrayList<BeatMap> revArrayList = new ArrayList<BeatMap>();
		for (int i = withoutDuplicates.size() - 1; i >= 0; i--) {
			revArrayList.add(withoutDuplicates.get(i));
		}
		return revArrayList;
	}

	public static ArrayList<BeatMap> getUserBeatmapsUsingOffset(String userID, int limit, int offset) throws Exception {
		String URI = "https://osu.ppy.sh/users/" + userID + "/beatmapsets/most_played?limit=" + limit + "&offset=" + offset;
		JSONArray root = getArrayFromURI(URI);

		ArrayList<BeatMap> beats = new ArrayList<BeatMap>();

		for (Object row : root) {
			BeatMap beat = new BeatMap((JSONObject) row);
			beats.add(beat);
		}

		return beats;
	}

	public static void writeBeatsToFile(ArrayList<BeatMap> beats, String inputFilePath) {
		for (BeatMap beat : beats) {
			try {
				FileWriter fw = new FileWriter(inputFilePath, true);
				String lineToAppend = "\r\nhttps://osu.ppy.sh/beatmapsets/" + beat.getBeatmapset_id();
				fw.write(lineToAppend);
				fw.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
}
