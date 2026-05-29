package com.degifetise.madguzoethiopiamobapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bucket_list")
public class BucketItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "region_name")
    private String regionName;

    @ColumnInfo(name = "planned_date")
    private String plannedDate;

    @ColumnInfo(name = "estimated_budget")
    private double estimatedBudget;

    @ColumnInfo(name = "priority_level")
    private String priorityLevel;

    public BucketItem(String regionName, String plannedDate, double estimatedBudget, String priorityLevel) {
        this.regionName = regionName;
        this.plannedDate = plannedDate;
        this.estimatedBudget = estimatedBudget;
        this.priorityLevel = priorityLevel;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }

    public String getPlannedDate() { return plannedDate; }
    public void setPlannedDate(String plannedDate) { this.plannedDate = plannedDate; }

    public double getEstimatedBudget() { return estimatedBudget; }
    public void setEstimatedBudget(double estimatedBudget) { this.estimatedBudget = estimatedBudget; }

    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }
}