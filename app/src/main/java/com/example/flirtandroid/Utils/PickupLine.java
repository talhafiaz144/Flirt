package com.example.flirtandroid.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PickupLine {
    private int id;
    private String name;
    private String line;
    private List<String> interests;
    private int favorite;
    private List<String> words;

    public PickupLine(int id, String name, String line, List<String> interests, int favorite, List<String> words) {
        this.id = id;
        this.name = name;
        this.line = line;
        this.interests = interests;
        this.favorite = favorite;
        this.words = words;
    }

    public PickupLine(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.line = jsonObject.getString("line");
        this.interests = new ArrayList<>();
        this.favorite = jsonObject.getInt("favorite");
        JSONArray interestsArray = jsonObject.getJSONArray("interest");
        for (int i = 0; i < interestsArray.length(); i++) {
            interests.add(interestsArray.getString(i));
        }
    }

    public PickupLine() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
