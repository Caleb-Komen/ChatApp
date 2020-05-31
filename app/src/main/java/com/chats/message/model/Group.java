package com.chats.message.model;

public class Group {
    private String groupId;
    private String groupName;
    private String creatorId;
    private String groupProfile;

    public Group() {
    }

    public Group(String groupId, String groupName, String creatorId, String groupProfile) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.creatorId = creatorId;
        this.groupProfile = groupProfile;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getGroupProfile() {
        return groupProfile;
    }

    public void setGroupProfile(String groupProfile) {
        this.groupProfile = groupProfile;
    }
}
