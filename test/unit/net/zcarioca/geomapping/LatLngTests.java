/*
 * Project: geomapping
 * 
 * Copyright (C) 2012 zcarioca.net
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.zcarioca.geomapping;

import static org.junit.Assert.*;
import grails.test.mixin.TestFor;

import net.zcarioca.geomapper.LatLng;

import org.junit.Test;

/**
 * Tests the latLng
 * 
 * 
 * @author zcarioca
 */
@TestFor(LatLng.class)
public class LatLngTests
{

   /**
    * Test method for {@link net.zcarioca.geomapper.LatLng#LatLng(double, double)}.
    */
   @Test
   public void testLatLngDoubleDouble()
   {
      LatLng latLng = new LatLng(40.7142, -74.0064); //new york
      assertEquals(40.7142, latLng.getLatitude(), 0);
      assertEquals(-74.0064, latLng.getLongitude(), 0);
      
      //test impossible
      latLng = new LatLng(-94, -193);
      assertEquals(-90, latLng.getLatitude(), 0);
      assertEquals(167, latLng.getLongitude(), 0);
   }

}
