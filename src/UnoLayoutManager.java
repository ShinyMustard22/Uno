import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Map.Entry;

public class UnoLayoutManager implements LayoutManager {
    
    private Map<Component, Rectangle> bounds = new LinkedHashMap<Component, Rectangle>();

        @Override
        public void addLayoutComponent(String name, Component comp) {
            bounds.put(comp, new Rectangle(comp.getPreferredSize()));
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            bounds.remove(comp);
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Rectangle rect = new Rectangle();
            for (Rectangle r : bounds.values()) {
                rect = rect.union(r);
            }
            return rect.getSize();
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        @Override
        public void layoutContainer(Container parent) {
            for (Entry<Component, Rectangle> e : bounds.entrySet()) {
                e.getKey().setBounds(e.getValue());
            }
        }

        public void setBounds(Component c, Rectangle bounds) {
            this.bounds.put(c, bounds);
        }


}