/**
This file is part of the Occleve (Open Content Learning Environment) mobile client
Copyright (C) 2007  Joe Gittings

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

@author Joe Gittings
@version 0.9.0
*/

package org.occleve.mobileclient;

import javax.microedition.lcdui.*;

/**This interface should be implemented by classes which can be excluded
from the end-user release of the project.
This is so they can be instantiated by Class.newInstance().*/
public interface Excludable
{
    public void setQAIndex(Integer i);
    public void setScreenToReturnTo(Displayable d);
    public void setTestFilename(String s);
    public void setTestRecordStoreID(Integer i);

    public void initialize() throws Exception;
}

