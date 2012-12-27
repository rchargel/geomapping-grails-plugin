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

import net.zcarioca.geomapper.BoundingBox;
import net.zcarioca.geomapper.LatLng;

import org.junit.Test;

/**
 * Tests the {@link BoundingBox}.
 * 
 * 
 * @author zcarioca
 */
@TestFor(BoundingBox.class)
public class BoundingBoxTests
{

   @Test
   public void testContainsSimple()
   {
      BoundingBox bb = new BoundingBox(new LatLng(40, -72), new LatLng(39, -70));
      
      assertTrue(bb.containsCoordinate(40, -72));
      assertTrue(bb.containsCoordinate(39, -70));
      assertTrue(bb.containsCoordinate(40, -70));
      assertTrue(bb.containsCoordinate(39, -72));
      assertTrue(bb.containsCoordinate(39.5, -71));
      assertFalse(bb.containsCoordinate(40.1, -72));
      assertFalse(bb.containsCoordinate(38.9, -72));
      assertFalse(bb.containsCoordinate(39.5, -72.1));
      assertFalse(bb.containsCoordinate(39.5, -69.9));
   }

   @Test
   public void testContainsSimpleHuge()
   {
      BoundingBox bb = new BoundingBox(new LatLng(40, -172), new LatLng(-39, 170));
      
      assertTrue(bb.containsCoordinate(40, -172));
      assertTrue(bb.containsCoordinate(-39, 170));
      assertTrue(bb.containsCoordinate(40, 170));
      assertTrue(bb.containsCoordinate(-39, -172));
      assertTrue(bb.containsCoordinate(0, -171));
      assertFalse(bb.containsCoordinate(40.1, -172));
      assertFalse(bb.containsCoordinate(-39.1, -172));
      assertFalse(bb.containsCoordinate(0, -172.1));
      assertFalse(bb.containsCoordinate(0, 170.1));
   }

   @Test
   public void testContainsCrossDateLine()
   {
      BoundingBox bb = new BoundingBox(new LatLng(40, 179.5), new LatLng(39, -179.5));
      
      assertTrue(bb.containsCoordinate(40, 179.5));
      assertTrue(bb.containsCoordinate(39, -179.5));
      assertTrue(bb.containsCoordinate(40, -179.5));
      assertTrue(bb.containsCoordinate(39, 179.5));
      assertTrue(bb.containsCoordinate(39.5, -179.9));
      assertTrue(bb.containsCoordinate(39.5, 179.9));
      assertFalse(bb.containsCoordinate(40.1, -179.5));
      assertFalse(bb.containsCoordinate(38.9, -179.5));
      assertFalse(bb.containsCoordinate(39.5, -179.4));
      assertFalse(bb.containsCoordinate(39.5, 179.4));
   }

}
