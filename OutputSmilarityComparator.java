package Indexing;

import java.util.Comparator;

public class OutputSmilarityComparator implements Comparator<Output>{

	@Override
	public int compare(Output o1, Output o2) {
		// TODO Auto-generated method stub
		if (o1.similarity < o2.similarity) return 1;
        if (o1.similarity > o2.similarity) return -1;
        return 0;
	}

}
