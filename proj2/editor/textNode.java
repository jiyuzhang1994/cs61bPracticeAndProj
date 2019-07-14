package editor;

import javafx.scene.text.Text;

public class textNode {
    public Text text;
    public textNode prev;
    public textNode next;

    public textNode(Text x, textNode m, textNode n) {
        text = x;
        prev = m;
        next = n;
    }
}
