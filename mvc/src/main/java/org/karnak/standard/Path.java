package org.karnak.standard;

/*
* module:tag:tag
* xa-xrf-series:00081111
* xa-xrf-series:00081150
* xa-xrf-series:00081111:00081150
* hmap.get(xa-xrf-series:00081111)
* hmap.get(xa-xrf-series)
* hmap.get(00081150)
* */

import java.util.ArrayList;
import java.util.Arrays;

public class Path {
    public String module;
    public ArrayList<String> tags;

    public Path(String path) {
        module = path.split(":", 2)[0];
        tags = new ArrayList<>(Arrays.asList(path.split(":", 2)[1].split(":")));
    }
    /*
    @Override
    public int compareTo(Path path) {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Path) {
            final UserResponse userAlbumResponse = (UserResponse) obj;
            return  userAlbumResponse.sub.compareTo(sub) == 0 &&
                    userAlbumResponse.isAdmin == isAdmin &&
                    userAlbumResponse.email.compareTo(email) == 0;
        }
        return false;
    }
    private int hashCode;
    @Override
    public int hashCode() {
        int result = hashCode;
        if(result == 0) {
            result = sub.hashCode();
            result = 31 * result + isAdmin.hashCode();
            result = 31 * result + email.hashCode();
            hashCode = result;
        }
        return result;
    }
    */
}
