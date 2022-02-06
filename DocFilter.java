/*
    Student Name: David Naughton
    Student Number: 320479
    Student Email: dna@student.unimelb.edu.au 
*/

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/* code adapted from
 https://stackoverflow.com/questions/60300755/how-to-remove-content-of-jtextfield-while-documentlistener-is-running */

class DocFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        StringBuilder sb = new StringBuilder(currentText);

        String newText = sb.insert(offset, string).toString();

        if (isValid(newText)) {
            super.insertString(fb, offset, string, attr);
        } else {
            Gui.updateStatusBar("Invalid input, please use [a-z]",false);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        StringBuilder sb = new StringBuilder(currentText);

        String newText = sb.replace(offset, offset + length, "").toString();

        if (isValid(newText)) {
            super.remove(fb, offset, length);
        } else {
            Gui.updateStatusBar("Invalid input, please use [a-z]",false);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        StringBuilder sb = new StringBuilder(currentText);

        String newText = sb.replace(offset, offset + length, text).toString();

        if (isValid(newText)) {
            super.replace(fb, offset, length, text, attrs);
        } else {
            Gui.updateStatusBar("Invalid input, please use [a-z]",false);
        }
    }   
        
    private boolean isValid(String text) {
        for (int i=0;i<text.length();i++) {
            int asc = text.charAt(i) -'a';
            if (asc<0 || asc >26) return false;
        }
        return true;
    }

}