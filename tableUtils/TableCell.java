package tableUtils;

/**
 * class contain the table cell content
 * this used to build the table row (used in TableRow.java class)
 */
public class TableCell {
    private final String content;
    private final int idx;

    public TableCell(int idx, String content) {
        this.idx = idx;
        this.content = content;
    }

    /**
     * get content of cell
     * @return
     */
    public String getContent() {
        return content;
    }

    public int getIdx() {
        return idx;
    }
}
