package com.example.instrukcije.Models;

import java.util.List;

public class ChatGroupModel {
	String ownerID, groupName;
	List<String> studentID, studentName;

	public ChatGroupModel() {
	}

	public ChatGroupModel(String ownerID, String groupName, List<String> studentID, List<String> studentName) {
		this.ownerID = ownerID;
		this.groupName = groupName;
		this.studentID = studentID;
		this.studentName = studentName;
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getStudentID() {
		return studentID;
	}

	public void setStudentID(List<String> studentID) {
		this.studentID = studentID;
	}

	public List<String> getStudentName() {
		return studentName;
	}

	public void setStudentName(List<String> studentName) {
		this.studentName = studentName;
	}
}
