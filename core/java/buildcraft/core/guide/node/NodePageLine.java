package buildcraft.core.guide.node;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import buildcraft.core.guide.PageLine;

public class NodePageLine {
    public final NodePageLine parent;
    public final PageLine pageLine;
    private final List<NodePageLine> children = Lists.newArrayList();
    public boolean expanded = true;

    public NodePageLine(NodePageLine parent, PageLine pageLine) {
        this.parent = parent;
        this.pageLine = pageLine;
    }

    public NodePageLine addChild(PageLine line) {
        NodePageLine node = new NodePageLine(this, line);
        children.add(node);
        return node;
    }

    public Iterable<PageLine> iterateNonNull() {
        return new Iterable<PageLine>() {
            @Override
            public Iterator<PageLine> iterator() {
                return new NodeIterator(false);
            }
        };
    }

    public Iterable<PageLine> iterateOnlyExpanded() {
        return new Iterable<PageLine>() {
            @Override
            public Iterator<PageLine> iterator() {
                return new NodeIterator(true);
            }
        };
    }

    public List<NodePageLine> getChildren() {
        return Collections.unmodifiableList(children);
    }

    private List<NodePageLine> getChildren(boolean skipIfNonExpanded) {
        if (skipIfNonExpanded || expanded) {
            return children;
        }
        return Collections.emptyList();
    }

    private class NodeIterator implements Iterator<PageLine> {
        private final boolean skipNonExpanded;
        private NodePageLine current;
        private int childrenDone = 0;

        NodeIterator(boolean skipNonExpanded) {
            this.skipNonExpanded = skipNonExpanded;
            current = NodePageLine.this;
        }

        @Override
        public boolean hasNext() {
            return next(true) != null;
        }

        @Override
        public PageLine next() {
            return next(false);
        }

        private PageLine next(boolean simulate) {
            NodePageLine current = this.current;
            int childrenDone = this.childrenDone;
            while (childrenDone == current.getChildren(skipNonExpanded).size()) {
                // Go to the parent
                NodePageLine child = current;
                current = current.parent;
                if (current == null) {
                    return null;
                }
                childrenDone = current.getChildren(skipNonExpanded).indexOf(child) + 1;
            }
            NodePageLine parent = current;
            current = parent.getChildren(skipNonExpanded).get(childrenDone++);
            childrenDone = 0;
            if (!simulate) {
                this.current = current;
                this.childrenDone = childrenDone;
            }
            return current.pageLine;

        }
    }
}
