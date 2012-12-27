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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A map location.
 * 
 * @author zcarioca
 */
public final class Location implements Serializable
{
   private static final long serialVersionUID = -7139816382487414183L;
   
   private String formattedAddress;
   private String name;
   private String number;
   private String street;
   private String city;
   private String stateProvince;
   private String postalCode;
   private String country;
   private LatLng location;
   private boolean partialMatch;
   
   public String getFormattedAddress()
   {
      return this.formattedAddress;
   }
   
   public void setFormattedAddress(String formattedAddress)
   {
      this.formattedAddress = formattedAddress;
   }

   public String getName()
   {
      return this.name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getNumber()
   {
      return this.number;
   }
   
   public void setNumber(String number)
   {
      this.number = number;
   }
   
   public String getStreet()
   {
      return this.street;
   }
   
   public void setStreet(String street)
   {
      this.street = street;
   }

   public String getCity()
   {
      return this.city;
   }

   public void setCity(String city)
   {
      this.city = city;
   }

   public String getStateProvince()
   {
      return this.stateProvince;
   }

   public void setStateProvince(String stateProvince)
   {
      this.stateProvince = stateProvince;
   }

   public String getPostalCode()
   {
      return this.postalCode;
   }

   public void setPostalCode(String postalCode)
   {
      this.postalCode = postalCode;
   }

   public String getCountry()
   {
      return this.country;
   }

   public void setCountry(String country)
   {
      this.country = country;
   }
   
   public LatLng getLocation()
   {
      return this.location;
   }
   
   public void setLocation(LatLng location)
   {
      this.location = location;
   }
   
   public boolean isPartialMatch()
   {
      return this.partialMatch;
   }
   
   public void setPartialMatch(boolean partialMatch)
   {
      this.partialMatch = partialMatch;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return HashCodeBuilder.reflectionHashCode(31, 3, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj)
   {
      return EqualsBuilder.reflectionEquals(this, obj, false);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return this.formattedAddress;
   }

}
