package editor;

import javafx.scene.text.Text;

public class Action {
    public int cursorX;
    public int cursorY;
    public boolean add;
    public boolean delete;
    public textNode nodeExecuted;
    public textNode nodePos;
    Text text;

    public Action(Text t, boolean isAdding, boolean isDeleting, textNode executedNode) {
        text = t;
        add = isAdding;
        delete = isDeleting;
        nodeExecuted = executedNode;
    }
}
