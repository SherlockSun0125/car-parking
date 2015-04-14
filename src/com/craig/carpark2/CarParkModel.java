package com.craig.carpark2;

//Craig Miller
//S1437151


public class CarParkModel {
	
	
	private int id;
	private String status;
	private int spaces;
	private int totalSpaces;
	private int spacesTaken;
	private String name;
	private double percentage;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getSpaces() {
		return spaces;
	}
	public void setSpaces(int spaces) {
		this.spaces = spaces;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTotalSpaces() {
		return totalSpaces;
	}
	public void setTotalSpaces(int totalSpaces) {
		this.totalSpaces = totalSpaces;
	}
	public int getSpacesTaken() {
		return spacesTaken;
	}
	public void setSpacesTaken(int spacesTaken) {
		this.spacesTaken = spacesTaken;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
}
