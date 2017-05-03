/*
 * Comic.java - This file is part of TFWC
 * Copyright (C) 2016  Thomas Kuenneth
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thomaskuenneth.tfwc;

import java.text.DateFormat;
import java.util.Date;
import javafx.scene.image.Image;

/**
 * This class implements a data structure that represents a comic.
 *
 * @author Thomas Kuenneth
 */
public class Comic {
    
    private static final DateFormat DF = DateFormat.getDateInstance(DateFormat.SHORT);
    
    public String title = "<no title>";
    public Date updated = new Date();
    public String summary = "<no summary>";
    public String url = "<no url>";
    public Image image = null;
    
    @Override
    public String toString() {
        return String.format("%s (%s)", title, DF.format(updated));
    }
}
