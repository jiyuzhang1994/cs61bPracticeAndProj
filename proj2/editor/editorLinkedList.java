package editor;

import javafx.scene.text.Text;

import java.util.Iterator;

public class editorLinkedList {
    private textNode sentinel; //sentinel at head;
    private textNode back; //sentinel at back;
    private textNode currText;

    private int size;

    public editorLinkedList() {
        size = 0;
        sentinel = new textNode(new Text(), null, null);
        back = new textNode(new Text(), sentinel, null);
        sentinel.next = back;
        currText = sentinel;
    }
    public editorLinkedList(Text x) {
        size = 1;
        textNode node = new textNode(x, null, null);

        //sentinel
        sentinel = new textNode(new Text(), null, node);
        node.prev = sentinel;

        //back
        back = new textNode(new Text(), node, null);
        node. next = back;
    }

    private class dataIterator implements Iterator {
        private textNode head;
        public dataIterator () {
            head = sentinel;
        }

        @Override
        public boolean hasNext() {
            return (head.next != back);
        }

        public textNode next() {
            if (this.hasNext()) {
                head = head.next;
            }
            return head;
        }
    }

    public Iterator iterator() {
        return new dataIterator();
    }

    public textNode getCurrText() {
        return currText;
    }

    public textNode getSentinel() {
        return sentinel;
    }

    public textNode getBack() {
        return back;
    }

    public void addText(Text x) {
        size += 1;
        textNode node = new textNode(x, currText, currText.next);
        currText.next.prev = node;
        currText.next = node;
        currText = node;
    }

    public Text delete() {
        if (size == 0 || currText == sentinel) {
            return null;
        }
        size -= 1;
        Text t = currText.text;
        currText = currText.prev;
        currText.next = currText.next.next;
        currText.next.prev = currText;
        return t;
    }

    public void moveLeft() {
        if (currText != sentinel) {
            currText = currText.prev;
        }
    }

    public void moveRight() {
        if (currText.next != back) {
            currText = currText.next;
        }
    }

    public void setCurrText(textNode node) {
        currText = node;
    }

    public boolean isEmpty() {
        return (sentinel.next == back);
    }

    public int size() {
        return size;
    }

    public void sizePlus() {
        size += 1;
    }
}
