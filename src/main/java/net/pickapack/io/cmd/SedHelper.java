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
package net.pickapack.io.cmd;

import java.util.List;

public class SedHelper {
    public static List<String> sed(String inputFileName, String outputFileName, String oldStuff, String newStuff) {
        return CommandLineHelper.invokeShellCommandAndGetResult("sed -e 's/" + oldStuff + "/" + newStuff + "/g' " + inputFileName + " > " + outputFileName);
    }

    public static List<String> sedInPlace(String fileName, String oldStuff, String newStuff) {
        return CommandLineHelper.invokeShellCommandAndGetResult("sed -i -e 's/^" + oldStuff + ".*/" + newStuff + "/g' " + fileName);
    }
}