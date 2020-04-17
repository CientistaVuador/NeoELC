package com.cien.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Node {

	private String name = null;
    private Node parent = null;

    private final List<Node> nodes = new ArrayList<>();
    private final Map<String, String> fields = new HashMap<>(16);

    private static String quote(String s) {
        StringBuilder b = new StringBuilder(128);
        b.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\':
                case '"':
                    b.append('\\');
            }
            b.append(c);
        }
        b.append('"');
        return b.toString();
    }

    public Node(String name) {
        this.name = name;
    }

    public Node getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField(String name) {
        return fields.get(name);
    }

    public void setField(String name, String value) {
        fields.put(name, value);
    }

    public Node[] getNodes() {
        return nodes.toArray(new Node[nodes.size()]);
    }

    public Node getNode(String name) {
        for (Node n : getNodes()) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }

    public void addNode(Node n) {
        nodes.add(n);
        n.parent = this;
    }

    public void removeNode(Node n) {
        if (nodes.remove(n)) {
            n.parent = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!o.getClass().equals(Node.class)) {
            return false;
        }
        Node f = (Node) o;
        if (f.parent != this.parent) {
            return false;
        }
        return f.fields == this.fields;
    }

    @Override
    public String toString() {
        return parent + name + "-" + fields.size() + "-" + nodes.size();
    }

    @SuppressWarnings("unchecked")
    public static String toString(Node n) {
        StringBuilder b = new StringBuilder(2048);

        b.append(quote(n.name));
        b.append(" < ");
        Entry<String, String>[] fields = n.fields.entrySet().toArray(new Entry[0]);
        Node[] nodes = n.getNodes();
        for (int i = 0; i < fields.length; i++) {
            Entry<String, String> e = fields[i];
            b.append(quote(e.getKey()));
            b.append(" = ");
            b.append(quote(e.getValue()));
            if (i != (fields.length - 1) || nodes.length > 0) {
                b.append(", ");
            }
        }
        for (int i = 0; i < nodes.length; i++) {
            Node f = nodes[i];
            b.append(Node.toString(f));
            if (i != (nodes.length - 1)) {
                b.append(", ");
            }
        }
        b.append(" >");

        return b.toString();
    }

    private static Node parse(CharReader s) {
        Node n = new Node(null);

        int chars = 0;
        boolean escape = false;
        boolean quote = false;
        StringBuilder b = new StringBuilder(128);
        String name = null;
        boolean equals = false;
        
        while (s.hasNext()) {
            char c = s.current();
            chars++;
            s.next();
            if (escape) {
            	b.append(c);
            	escape = false;
            	continue;
            }
            if (c == '\\') {
            	escape = true;
            	continue;
            }
            if (c == '"') {
            	quote = !quote;
            	if (!quote && equals) {
            		equals = false;
            		n.setField(name, b.toString());
            		name = null;
            		b.setLength(0);
            		chars = 0;
            	}
            	continue;
            }
            if (quote) {
            	b.append(c);
            	continue;
            }
            if (c == '=') {
            	equals = true;
            	name = b.toString();
            	b.setLength(0);
            	continue;
            }
            if (c == '<') {
            	if (n.name == null) {
            		n.name = b.toString();
            		b.setLength(0);
            		chars = 0;
            	} else {
            		s.back(chars);
            		n.addNode(parse(s));
            		chars = 0;
            		b.setLength(0);
            	}
            	continue;
            }
            if (c == '>') {
            	break;
            }
        }
        return n;
    }
    
    public static Node parse(String s) {
        return parse(new CharReader(s.toCharArray()));
    }

    private static class CharReader {

        private final char[] array;
        private int index = 0;

        public CharReader(char[] array) {
            this.array = array;
        }

        public char current() {
            return array[index];
        }

        public void next() {
            index++;
        }

        public void back(int i) {
            index -= i;
        }

        public boolean hasNext() {
            return index < array.length;
        }
    }

}
