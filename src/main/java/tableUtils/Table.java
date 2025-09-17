package tableUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * this class used for extracting tables from files that are used in pdfUtils
 */
public class Table {

    private final int pageIdx;
    private final List<TableRow> rows = new ArrayList<>();
    private final int columnsCount;

    public Table(int idx, int columnsCount) {
        this.pageIdx = idx;
        this.columnsCount = columnsCount;
    }/**/

    public int getPageIdx() {
        return pageIdx;
    }

    public List<TableRow> getRows() {
        return rows;
    }
}
