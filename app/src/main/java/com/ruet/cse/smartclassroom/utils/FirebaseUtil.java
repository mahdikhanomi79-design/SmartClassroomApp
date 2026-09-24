package com.ruet.cse.smartclassroom.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
public class FirebaseUtil {

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_ROUTINES = "routines";
    public static final String COLLECTION_CLASSROOMS = "classrooms";
    public static final String COLLECTION_TEACHERS = "teachers";

    public static String currentUid() {
        if (getAuth().getCurrentUser() == null) return null;
        return getAuth().getCurrentUser().getUid();
    }
}
