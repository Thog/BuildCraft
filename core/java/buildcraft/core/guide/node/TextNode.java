package buildcraft.core.guide.node;

// FIXME: Is this even required? (TextNode)
public class TextNode {
    public final String text;
    public final int size;
    public final TextNode child;

    public static TextNode pack(TextNode[] nodes) {
        if (nodes == null || nodes.length == 0) {
            return new TextNode("", 1, null);
        }
        if (nodes.length == 1) {
            return nodes[0];
        }
        TextNode bottom = nodes[nodes.length - 1];
        TextNode current = bottom;
        for (int i = nodes.length - 1; i >= 0; i--) {
            TextNode textNode = nodes[i];
            if (textNode.child == null) {
                current = new TextNode(textNode.text, textNode.size, bottom);
                bottom = current;
            } else {
                throw new IllegalArgumentException("Was given a text node with children! " + textNode);
            }
        }
        return current;
    }

    public TextNode(String text, int size, TextNode child) {
        this.text = text;
        this.size = size;
        this.child = child;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TextNode [text=");
        builder.append(text);
        builder.append(", size=");
        builder.append(size);
        builder.append(", child=");
        builder.append(child);
        builder.append("]");
        return builder.toString();
    }
}
