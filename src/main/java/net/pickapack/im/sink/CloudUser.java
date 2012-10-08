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
package net.pickapack.im.sink;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Min Cai
 */
public class CloudUser {
    private String id;
    private List<InstantMessage> instantMessages;

    /**
     *
     * @param id
     */
    public CloudUser(String id) {
        this.id = id;
        this.instantMessages = new ArrayList<InstantMessage>();
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public List<InstantMessage> getInstantMessages() {
        return instantMessages;
    }
}