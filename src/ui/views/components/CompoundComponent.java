package ui.views.components;

import javafx.scene.Group;

public interface CompoundComponent {

    enum NodeType {
        ADD, REMOVE
    }

    Group createComponent(String personId);
}
