package com.epet.epet.backend.business;

import android.content.Context;
import android.net.Uri;
import com.epet.epet.R;
public class PetBusiness {
    Context _context;
    public PetBusiness(Context context) {
        _context = context;
    }
    public String addPetCheck(String petName, String Birthdate, Uri uri) {
        if (petName.isEmpty() || Birthdate.isEmpty() || uri == null ){
            return _context.getString(R.string.auth_data_empty);
        }
        return null;
    }

    public String addRehomingPetCheck(String desc) {
        if (desc.isEmpty()){
            return _context.getString(R.string.auth_data_empty);
        }
        return null;
    }
}
