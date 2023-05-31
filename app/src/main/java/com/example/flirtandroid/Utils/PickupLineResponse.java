package com.example.flirtandroid.Utils;

import java.util.List;

public class PickupLineResponse {
    private boolean status;
    private String message;
    private List<PickupLineData> data;

    public boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<PickupLineData> getData() {
        return data;
    }

    public static class PickupLineData {
        private int id;
        private String fk_user_id;
        private String name;
        private String line;
        private String favorite;
        private List<String> interest;
        private String feedback;
        private String deleted;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public String getFk_user_id() {
            return fk_user_id;
        }

        public String getName() {
            return name;
        }

        public String getLine() {
            return line;
        }

        public String getFavorite() {
            return favorite;
        }

        public List<String> getInterest() {
            return interest;
        }

        public String getFeedback() {
            return feedback;
        }

        public String getDeleted() {
            return deleted;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }
}

