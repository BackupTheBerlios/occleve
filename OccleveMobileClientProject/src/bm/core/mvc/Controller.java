/*
 * Copyright (c) 2006 Elondra S.L. All Rights Reserved.
 */
package bm.core.mvc;
/* -----------------------------------------------------------------------------
    OpenBaseMovil Core Library, foundation of the OpenBaseMovil database and tools
    Copyright (C) 2004-2008 Elondra S.L.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.
    If not, see <a href="http://www.gnu.org/licenses">http://www.gnu.org/licenses</a>.
----------------------------------------------------------------------------- */
/*
 * File Information
 *
 * Created by       : narciso
 * Last modified by : $Author: joe_gittings $
 * Last modified on : $Date: 2008/07/04 18:42:54 $
 * Revision         : $Revision: 1.1 $
 */

/**
 * A Controller handles user interaction and notifies and changes views.
 *
 * @author <a href="mailto:narciso@elondra.com">Narciso Cerezo</a>
 * @version $Revision: 1.1 $
 */
public interface Controller
{
    /**
     * Handle an event.
     *
     * @param event event to handle
     */
    void handle( ControllerEvent event );

    /**
     * When a view is released.
     * @param view view released
     */
    void onRelease( View view );
}
