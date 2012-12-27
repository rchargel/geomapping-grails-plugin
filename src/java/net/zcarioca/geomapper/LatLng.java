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
 * A latitude and longitude pair.
 * 
 * 
 * @author zcarioca
 */
public final class LatLng implements Serializable
{
   private static final long serialVersionUID = 2455974495640636414L;

   /** The 'Y' coordinate on a globe in decimal degrees. */
   private double latitude;
   
   /** The 'X' coordinate on a globe in decimal degrees. */
   private double longitude;
   
   public LatLng() { }
   
   public LatLng(double latitude, double longitude) {
      setLatitude(latitude);
      setLongitude(longitude);
   }
   
   public double getLatitude()
   {
      return this.latitude;
   }
   
   public void setLatitude(double latitude)
   {
      if (latitude < -90) {
         this.latitude = -90;
      } else if (latitude > 90) {
         this.latitude = 90;
      } else {
         this.latitude = latitude;
      }
   }
   
   public double getLongitude()
   {
      return this.longitude;
   }
   
   public void setLongitude(double longitude)
   {
      if (longitude < -180) {
         longitude = 180 - (Math.abs(longitude) - 180);
      }
      if (longitude > 180) {
         longitude = (180 - (Math.abs(longitude) - 180)) * -1;
      }
      this.longitude = longitude;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(latitude);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(longitude);
      result = prime * result + (int) (temp ^ (temp >>> 32));
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
      LatLng other = (LatLng) obj;
      if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
         return false;
      if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
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
