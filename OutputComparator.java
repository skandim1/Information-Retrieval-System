package Indexing;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class OutputComparator implements Comparator<Output>{

	private List<Comparator<Output>> listComparators;
	 
    @SafeVarargs
    public OutputComparator(Comparator<Output>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }
	@Override
	public int compare(Output o1, Output o2) {
		for (Comparator<Output> comparator : listComparators) {
            int result = comparator.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }
		return 0;
	}

}
