/*
 *     This file is part of Prisma.
 *
 *     Prisma is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Prisma is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Prisma.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.condomitti.prisma;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Bean_Contact implements Serializable {
    private int _id;
    private int _raw_id;
    private String name;

    /**
     * For E-mail and Phone structures the format of the List should be kept like this:
     * index(0) - type (String)
     * index(1) - data
     * index(2) - type (standard int from Android API)
     * index(3) - row id
     */
    private ArrayList<ArrayList<String>> emails;
    private ArrayList<ArrayList<String>> phones;

    public Bean_Contact(int _id, int _raw_id, String name,
                        ArrayList<ArrayList<String>> emails,
                        ArrayList<ArrayList<String>> phones) {
        super();
        this._id = _id;
        this._raw_id = _raw_id;
        this.name = name;
        this.emails = emails;
        this.phones = phones;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_raw_id() {
        return _raw_id;
    }

    public void set_raw_id(int _raw_id) {
        this._raw_id = _raw_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ArrayList<String>> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<ArrayList<String>> emails) {
        this.emails = emails;
    }

    public ArrayList<ArrayList<String>> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<ArrayList<String>> phones) {
        this.phones = phones;
    }


}