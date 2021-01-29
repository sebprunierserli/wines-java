package org.example.misc.json.contact;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBaseObject {

    protected void customizeJsonObject(JsonObject jsonObject) {
    }

    public final String toJson() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonElement jsonElement = gson.toJsonTree(this);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        customizeJsonObject(jsonObject);
        return gson.toJson(jsonObject);
    }

}
