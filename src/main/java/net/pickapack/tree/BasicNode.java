/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.tree;

import java.util.ArrayList;
import java.util.List;

public class BasicNode implements Node {
    private Object value;
    private List<Node> children;

    public BasicNode(Object value) {
        this.value = value;
        this.children = new ArrayList<Node>();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    public static void main(String[] args) {
        BasicNode node0 = new BasicNode(0);
        BasicNode node1 = new BasicNode(1);
        BasicNode node2 = new BasicNode(2);
        BasicNode node3 = new BasicNode(3);
        BasicNode node4 = new BasicNode(4);
        node0.getChildren().add(node1);
        node0.getChildren().add(node2);
        node0.getChildren().add(node3);
        node0.getChildren().add(node4);
        NodeHelper.print(node0);
    }
}
