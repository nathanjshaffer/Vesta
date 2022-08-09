package com.eteks.sweethome3d.model;

import java.util.Arrays;

import com.eteks.sweethome3d.model.Wall;

public class WallSide extends HomeObject {

  private static final long serialVersionUID = 1L;
  
  public static final int LEFT_SIDE = 0;
  public static final int RIGHT_SIDE = 1;
  public static final int START = 0;
  public static final int END = 1;

  private Wall          wall;
  private int           side;
  private Integer             color;
  private HomeTexture         texture;
  private float               shininess;
  private Baseboard           baseboard;

  public WallSide(Wall wall, int side) {
    this.wall = wall;
    this.side = side;
  }

  public Wall getWall() {
    return this.wall;
  }

  public int getSide() {
    return this.side;
  }
  

  public Integer getColor() {
    return this.color;
  }
  
  public void setColor(Integer color) {
    this.color = color;
  }

  public HomeTexture getTexture() {
    return this.texture;
  }

  public void setTexture(HomeTexture texture) {
    this.texture = texture;
  }

  public float getShininess() {
    return this.shininess;
  }

  public void setShininess(float shininess) {
    this.shininess = shininess;
  }

  public Baseboard getBaseboard() {
    return this.baseboard;
  }

  public void setBaseboard(Baseboard baseboard) {
    this.baseboard = baseboard;
  }
  
  public WallSide Clone() {
    WallSide wallSide = new WallSide(this.wall, this.side);

    wallSide.color = this.color;
    wallSide.texture = this.texture;
    wallSide.shininess = this.shininess;
    wallSide.baseboard = this.baseboard;
    return wallSide;
  }
  

  public WallSideState getState() {
    WallSideState state = new WallSideState(this);
    return state;
  }

  /**
   * gets start point of wallside shape in counterclockwise rotation.
   * @param includeBaseboards
   * @return
   */
  
  public Vector2D getUnjoindStartPoint(boolean includeBaseboards) {
    float distance = this.wall.getThickness() / 2;
    if(includeBaseboards) {
      distance += baseboard.getThickness();
    }
    float angle = 0l;
    
    Vector2D start;
    if(this.side == this.RIGHT_SIDE) {
      start = wall.getEndPoint().toVector();
    } else {
      start = wall.getStartPoint().toVector();
      distance = distance * -1;
    }
    

    Float arcExtent = this.wall.getArcExtent();
    if (arcExtent != null
        && arcExtent.floatValue() != 0) {
      angle = start.getAngle(wall.getArcCircleCenter());
    }
    else {
      angle = wall.getAngle() - (float)Math.PI/2;
    }
    return start.movePoint( angle, distance);
  }

  /**
   * gets end point of wallside shape in counterclockwise rotation.
   * @param includeBaseboards
   * @return
   */
  
  public Vector2D getUnjoindEndPoint(boolean includeBaseboards) {
    float distance = this.wall.getThickness() / 2;
    if(includeBaseboards) {
      distance += baseboard.getThickness();
    }
    float angle = 0l;
    
    Vector2D end;
    if(this.side == this.RIGHT_SIDE) {
      end = wall.getStartPoint().toVector();
    } else {
      end = wall.getEndPoint().toVector();
      distance = distance * -1;
    }
    
    Float arcExtent = this.wall.getArcExtent();
    if (arcExtent != null
        && arcExtent.floatValue() != 0) {
      angle = end.getAngle(wall.getArcCircleCenter());
    }
    else {
      angle = wall.getAngle() - (float)Math.PI/2;
    }
    return end.movePoint( angle, distance);
  }
  
  /**
   * gets the line segment for this WallSide closest to given wall endpoint.
   * @return
   */
  
  public Line2D getJoinLine(WallPoint wallPoint, boolean includeBaseboards) {
    Vector2D[] points = getUnjoinedShapePoints(includeBaseboards);
    
    if((wall.getStartPoint() == wallPoint && this.side == this.LEFT_SIDE) || (wall.getEndPoint() == wallPoint && this.side == this.RIGHT_SIDE)) {
      return new Line2D(points[0], points[1]);
    }
    else {
      return new Line2D(points[points.length-1], points[points.length-2]);
    }
  }
  
  public WallSide getNearestWallside(int direction) {
    Wall joinedWall;
    WallPoint wallPoint;
    int joinedWallSide = this.side;
    if(direction == START) {
      if(wall.startIsJoined()) {
        wallPoint = this.wall.getStartPoint();
        joinedWall = this.wall.getNearestJoinedWall(wallPoint, !(this.side == this.RIGHT_SIDE), 0f);
        if(joinedWall.isStartPoint(wallPoint)) joinedWallSide ^= 1;
        
      } else return null;
        
    }
    else {
      if(wall.endIsJoined()) {
        wallPoint = this.wall.getEndPoint();
        joinedWall = this.wall.getNearestJoinedWall(wallPoint, !(this.side == this.LEFT_SIDE), 0f);
        if(!joinedWall.isStartPoint(wallPoint)) joinedWallSide ^= 1;
      } else return null;
      
    }

    
    return joinedWall.getSide(joinedWallSide);
  }


  /**
   * gets start point of wallside shape in counterclockwise rotation.
   * @param includeBaseboards
   * @return
   */
  
  public Vector2D [] getShapePoints(boolean includeBaseboards) {
    Vector2D [] points =  getUnjoinedShapePoints(includeBaseboards);
    System.out.println("side");
    System.out.println(Arrays.asList(points));
    int startIndex = 0, endIndex = points.length - 1;
    if(this.side == this.RIGHT_SIDE) {
      startIndex = points.length - 1;
      endIndex = 0;
    }
    WallSide wallSide = this.getNearestWallside(START);
    if(wallSide != null)
    {
      Vector2D point = Line2D.intersection(this.getJoinLine(this.wall.getStartPoint(), includeBaseboards), wallSide.getJoinLine(this.wall.getStartPoint(), includeBaseboards));
      if(point != null)
      points[startIndex] =  point;
    }
    
    wallSide = this.getNearestWallside(END);
    if(wallSide != null)
    {
      Vector2D point = Line2D.intersection(this.getJoinLine(this.wall.getEndPoint(), includeBaseboards), wallSide.getJoinLine(this.wall.getEndPoint(), includeBaseboards));
      if(point != null)
      points[endIndex] = point;
    }

    System.out.println(Arrays.asList(points));
    return points;
  }
  
  /**
   * Computes the rectangle or the circle arc of a wall according to its thickness
   * and possibly the thickness of its baseboards.
   */
  public Vector2D [] getUnjoinedShapePoints(boolean includeBaseboards) {
    Float arcExtent = this.wall.getArcExtent();
    if (arcExtent != null
        && arcExtent.floatValue() != 0
        && this.wall.getStartPoint().toVector().distanceSq(this.wall.getEndPoint().toVector()) > 1E-10) {
      Vector2D arcCircleCenter = this.wall.getArcCircleCenter();
      
      float startAngle = getUnjoindStartPoint(includeBaseboards).getAngle(wall.getArcCircleCenter());
      float arcCircleRadius = (float)arcCircleCenter.distance(this.wall.getStartPoint().toVector());
      float ArcLength = arcCircleRadius * Math.abs(arcExtent);
      float angleDelta = arcExtent / (float)Math.sqrt(ArcLength);
      int angleStepCount = (int)(arcExtent / angleDelta) - 1;
      
      if(this.side == this.RIGHT_SIDE) {
        angleDelta = angleDelta * -1;
      }
      Vector2D [] wallPoints = new Vector2D [angleStepCount +1];
      Vector2D point = this.getUnjoindStartPoint(includeBaseboards);
      wallPoints[0] = point;
      for(int i = 1; i <= angleStepCount; i++) {
        point = point.movePointAboutArc(arcCircleCenter, angleDelta);
        wallPoints[i] = point;
      }
      wallPoints[angleStepCount] = this.getUnjoindEndPoint(includeBaseboards);
      
      
      return wallPoints;
    } else {
      return new Vector2D [] {this.getUnjoindStartPoint(includeBaseboards), this.getUnjoindEndPoint(includeBaseboards)};
    }
  }
  
  public static class WallSideState {   
    public final WallSide           wallSide;
    public final int                side;
    public final Integer            color;
    public final HomeTexture        texture;
    public final float              shininess;
    public final Baseboard          baseboard;

    public WallSideState(WallSide wallSide) {
      this.wallSide = wallSide;
      this.side = wallSide.side;
      this.color = wallSide.color;
      this.texture = wallSide.texture;
      this.shininess = wallSide.shininess;
      this.baseboard = wallSide.baseboard;
    }
  }
}
