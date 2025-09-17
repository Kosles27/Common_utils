package tableUtils;

import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A class to represent ranges of values.
 * A range is defined to contain all the values between the minimum and maximum values
 * Used to get line range to build the table in pdfUtils
 */
public class TrapRangeBuilder {
    private final Logger logger = LoggerFactory.getLogger(TrapRangeBuilder.class);
    private final List<Range<Integer>> ranges = new ArrayList<>();

    public TrapRangeBuilder addRange(Range<Integer> range) {
        ranges.add(range);
        return this;
    }

    /**
     * The result will be ordered by lowerEndpoint ASC
     *
     * @return
     */
    public List<Range<Integer>> build() {
        List<Range<Integer>> retVal = new ArrayList<>();
        //order range by lower Bound
        Collections.sort(ranges, new Comparator<Range>() {
            @Override
            public int compare(Range o1, Range o2) {
                return o1.lowerEndpoint().compareTo(o2.lowerEndpoint());
            }
        });

        for (Range<Integer> range : ranges) {
            if (retVal.isEmpty()) {
                retVal.add(range);
            } else {
                Range<Integer> lastRange = retVal.get(retVal.size() - 1);
                if (lastRange.isConnected(range)) {
                    Range newLastRange = lastRange.span(range);
                    retVal.set(retVal.size() - 1, newLastRange);
                } else {
                    retVal.add(range);
                }
            }
        }
        //debug
        logger.debug("Found " + retVal.size() + " trap-range(s)");
        //return
        return retVal;
    }
}