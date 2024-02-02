package beat;

import java.util.Comparator;

public class BeatComparator implements Comparator<BeatMap> {

	@Override
	public int compare(BeatMap b1, BeatMap b2) {
		return Integer.compare(b1.getBeatmapset_id(), b2.getBeatmapset_id());
	}

}