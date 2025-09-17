package objectsUtils;

import org.apache.commons.text.diff.CommandVisitor;

/**
 * Useful to compare 2 Strings
 * Algorithm executes comparison of ‘left’ & ‘right’ Strings character by character. *
 * If a character is present in both ‘left’ & ‘right’ – Referred as ‘KeepCommand‘
 * If a character is present in ‘left’ file but not in ‘right’, that means it need to be deleted from ‘left’ to match ‘right. – Referred as ‘DeleteCommand‘
 * If a character is not present in ‘left’ but present in ‘right’, that means it needs to be inserted into ‘left’ to match right – Referred as ‘InsertCommand‘
 * @author Genosar.dafna
 * @since 11.01.2024
 */
public class StringCommandsVisitor implements CommandVisitor<Character> {

    String left = "";
    String right = "";

    @Override
    public void visitKeepCommand(Character c) {
        // Character is present in both files.
        left = left + c;
        right = right + c;
    }

    @Override
    public void visitInsertCommand(Character c) {
        /*
         * Character is present in right file but not in left. Method name
         * 'InsertCommand' means, c need to insert it into left to match right.
         */
        right = right + "((" + c + "))";
    }

    @Override
    public void visitDeleteCommand(Character c) {
        /*
         * Character is present in left file but not right. Method name 'DeleteCommand'
         * means, c need to be deleted from left to match right.
         */
        left = left + "{{" + c + "}}";
    }
}
