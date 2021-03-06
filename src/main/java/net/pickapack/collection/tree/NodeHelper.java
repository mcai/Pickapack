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
package net.pickapack.collection.tree;

/**
 * Node helper.
 *
 * @author Min Cai
 */
public class NodeHelper {
    /**
     * Print the node.
     *
     * @param node the node
     */
    public static void print(Node node) {
        print(node, "", true);
    }

    private static void print(Node node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.getValue());
        if (node.getChildren() != null) {
            for (int i = 0; i < node.getChildren().size() - 1; i++) {
                Node childNode = node.getChildren().get(i);
                print(childNode, prefix + (isTail ? "    " : "│   "), false);
            }
            if (node.getChildren().size() >= 1) {
                Node lastNode = node.getChildren().get(node.getChildren().size() - 1);
                print(lastNode, prefix + (isTail ? "    " : "│   "), true);
            }
        }
    }
}
