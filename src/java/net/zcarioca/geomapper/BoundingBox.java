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
package net.zcarioca.geomapper;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Defines a bounding box.
 * 
 * 
 * @author zcarioca
 */
public final class BoundingBox implements Serializable
{
   private static final long serialVersionUID = -6188280763035812173L;

   private LatLng northWest;
   private LatLng southEast;

   /**
    * Default constructor.
    */
   public BoundingBox() { }

   /**
    * Constructor.
    * 
    * @param northWest The north-west coordinate.
    * @param southEast The south-east coordinate.
    */
   public BoundingBox(LatLng northWest, LatLng southEast)
   {
      this.northWest = northWest;
      this.southEast = southEast;
   }

   public LatLng getNorthWest()
   {
      return this.northWest;
   }

   public void setNorthWest(LatLng northWest)
   {
      this.northWest = northWest;
   }

   public LatLng getSouthEast()
   {
      return this.southEast;
   }

   public void setSouthEast(LatLng southEast)
   {
      this.southEast = southEast;
   }
   
   /**
    * Tests to see if a given coordinate falls within or on the bounding box.
    * @param coordinate The coordinate
    * @return Returns true if the coordinate falls within or on the line of the bounding box.
    */
   public boolean containsCoordinate(LatLng coordinate) 
   {
      return containsCoordinate(coordinate.getLatitude(), coordinate.getLongitude());
   }

   /**
    * Tests to see if a given coordinate falls within or on the bounding box.
    * @param latitude The Y coordinate
    * @param longitude The X coordinate
    * @return Returns true if the coordinate falls within or on the line of the bounding box.
    */
   public boolean containsCoordinate(double latitude, double longitude) 
   {
      // test latitude
      if (latitude < southEast.getLatitude() || latitude > northWest.getLatitude()) {
         return false;
      }
      // if simple bounding box
      if (southEast.getLongitude() > northWest.getLongitude()) {
         return longitude <= southEast.getLongitude() && longitude >= northWest.getLongitude();
      } else {
         // cross date line
         if (longitude < northWest.getLongitude() && longitude > southEast.getLongitude()) {
            return false;
         }
      }
      
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((northWest == null) ? 0 : northWest.hashCode());
      result = prime * result + ((southEast == null) ? 0 : southEast.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BoundingBox other = (BoundingBox) obj;
      if (northWest == null)
      {
         if (other.northWest != null)
            return false;
      }
      else if (!northWest.equals(other.northWest))
         return false;
      if (southEast == null)
      {
         if (other.southEast != null)
            return false;
      }
      else if (!southEast.equals(other.southEast))
         return false;
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
   }
}
