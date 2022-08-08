package com.eteks.sweethome3d.model;

import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Point2D;
import com.eteks.sweethome3d.model.Vector2D;
import com.eteks.sweethome3d.model.Wall.Property;
import com.eteks.sweethome3d.model.Solver2D.Constraint2D;;

public class WallPoint extends HomeObject{
  
  private static final long serialVersionUID = 1L;
  
  private Vector2D                      location;
  private Level                         level;
  public static Home                    home;
  private ArrayList<Constraint2D>       constraints;
  
  
  public ArrayList<Wall> joinedWalls = new ArrayList<Wall>();

  public WallPoint() {
    this(0f, 0f, null);
  }
  
  public WallPoint(float x, float y) {
    this(x, y, null);
  }
  
  public WallPoint(Wall joinedWall) {
    this(0f, 0f, joinedWall);
  }

  public WallPoint(float x, float y, Wall joinedWall) {
    this(createId("wallPoint"), x, y, joinedWall);
  }

  public WallPoint(String id, float x, float y, Wall joinedWall) {
    super(id);
    location = new Vector2D(x, y); 
    if(joinedWall != null) {
      joinedWalls.add(joinedWall);
    }
    //System.out.println(id);
  }
  
  public Wall[] getJoinedWalls() {
    return this.joinedWalls.toArray(new Wall[this.joinedWalls.size()]);
  }

  /**
   * Returns the level which this wallPoint belongs to.
   * @since 3.4
   */
  public Level getLevel() {
    return this.level;
  }
  
  public Vector2D toVector() {
    return location;
  }
  
  public float getX() {
    return location.x;
  }

  public float getY() {
    return location.y;
  }
  
  public void setX(float x) {
    float oldX = location.x;
    location.x = x;
    for(int i = 0; i < joinedWalls.size(); i++) {
      Wall wall = joinedWalls.get(i);
      wall.clearPointsCache();
      wall.clearArcCircleCenterCache();
      if(wall.isStartPoint(this)) {
        wall.firePropertyChange(Property.X_START.name(), oldX, x);
      }
      else {
        wall.firePropertyChange(Property.X_END.name(), oldX, x);
      }
    }
  }

  public void setY(float y) {
    float oldY = location.y;
    location.y = y;
    for(int i = 0; i < joinedWalls.size(); i++) {
      Wall wall = joinedWalls.get(i);
      wall.clearPointsCache();
      wall.clearArcCircleCenterCache();
      if(wall.isStartPoint(this)) {
        wall.firePropertyChange(Property.Y_START.name(), oldY, y);
      }
      else {
        wall.firePropertyChange(Property.Y_END.name(), oldY, y);
      }
    }
  }
  
  public void setLocation(float x, float y) {
    setX(x);
    setY(y);
  }
  
  public void setLocation(Point2D point) {
    setLocation((float)point.getX(), (float)point.getY());
  }
  
  public void addWall(Wall wall) {
    if(!joinedWalls.contains(wall)) {
      joinedWalls.add(wall);
    }
    System.out.println("add Wall");
  }
  
  public boolean containsJoinedWall(Wall wall) {
    return joinedWalls.contains(wall);
  }
  
  
  public void clearJoinedWalls() {
    joinedWalls.clear();
  }

  /**
   * append walls from one point to this point keeping this point
   * @param oldPoint
   */
  
  public void combineWallPoints(List<WallPoint> oldPoints) {
    for(WallPoint oldpoint : oldPoints) {
      combineWallPoints(oldpoint);
    }
  }
  
  /**
   * append walls from one point to this point keeping this point
   * @param oldPoint
   */
  
  public void combineWallPoints(WallPoint oldPoint) {
    if(oldPoint == this) return;
    joinedWalls.addAll(oldPoint.joinedWalls);
    for (int i = 0; i < oldPoint.joinedWalls.size(); i++) {
      Wall wall = oldPoint.joinedWalls.get(i);
      if(wall.isStartPoint(oldPoint))
        wall.setStartPoint(this);
      else
        wall.setEndPoint(this);
      oldPoint.joinedWalls.remove(wall);
    }
    if(oldPoint.joinedWalls.isEmpty()) {
      home.deleteWallPoint(oldPoint);
    }
  }
  
  public static List<WallPoint> getCommonPoints(List<Wall> walls) {
    List<WallPoint> points = new ArrayList<WallPoint>();
    for(int i = 0; i < walls.size() - 1; i++) {
      Wall wall = walls.get(i);
      WallPoint point = null;
      for(int end = 0; end < 2; end++) {
        point = end == 0 ? wall.getStartPoint() : wall.getEndPoint();
        if(walls.get(i+1).getStartPoint() == point && !points.contains(point)) points.add(point);
        if(walls.get(i+1).getEndPoint() == point&& !points.contains(point)) points.add(point);
      }
      
    }
    return points;
  }
  
  
  /**
   * move wall  from this wall point to another wall point. If destination point is null, create new point with current coordinates
   * @param unJoinedWall
   * @param newPoint
   */
  
  public void switchJoinedWall(Wall unJoinedWall, WallPoint newPoint) {
    if(newPoint == this) return;
    if(newPoint == null) {
      newPoint = new WallPoint(this.location.x, this.location.y, unJoinedWall);
      home.addWallPoint(newPoint);
    }
    if(unJoinedWall.getStartPoint() == this) {
      unJoinedWall.setStartPoint(newPoint);
    }
    else if(unJoinedWall.getEndPoint() == this){
      unJoinedWall.setEndPoint(newPoint);
    }
    joinedWalls.remove(unJoinedWall);
    if(joinedWalls.isEmpty()) {
      home.deleteWallPoint(this);
    }
  }
  


  public static List<WallPoint> clone(List<WallPoint> wallPoints) {
    ArrayList<WallPoint> wallPointsCopy = new ArrayList<WallPoint>(wallPoints.size());
    for (WallPoint wallPoint : wallPoints) {
      wallPointsCopy.add(wallPoint.clone());
    }
    return wallPointsCopy;
  }

  public WallPoint clone() {
    WallPoint newPoint = (WallPoint)super.clone();
    newPoint.location = new Vector2D(this.location.x, this.location.y);
    newPoint.joinedWalls = new ArrayList<Wall>();
    return newPoint;
  }
  
  
  /**
   * @return Returns the constraints.
   */
  public Constraint2D [] getConstraints() {
    return this.constraints.toArray(new Constraint2D [this.constraints.size()]);
  }

  public PointState getState() {
    return new PointState(this);
  }
   

  public static class PointState {
    public final WallPoint point;
    public final float x;
    public final float y;
    
    PointState(WallPoint point){
      this(point, point.location.x, point.location.y);
    }
    
    PointState(WallPoint point, float x, float y) {
      this.point = point;
      this.x = x;
      this.y = y;
      
    }
    
    public void resetPoint(WallPoint point) {
      point.location.x = x;
      point.location.y = y;
    }
  }
}

