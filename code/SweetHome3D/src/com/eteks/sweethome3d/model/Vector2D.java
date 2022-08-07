package com.eteks.sweethome3d.model;

import java.awt.geom.Point2D;
import java.lang.Float;

public class Vector2D extends Point2D.Float { 

  public Vector2D() {
    this(0f, 0f);
  }
  
  public Vector2D(Point2D point) {
    this((float)point.getX(), (float)point.getY());
  }

  public Vector2D(double x, double y){
    this((float)x, (float)y);
  }
  
  public Vector2D(float x, float y){
    super(x, y);
  }
  
  public Vector2D movePoint( double angle, double distance) {
    return movePoint(this, angle, distance);       
  }  
  
  public static Vector2D movePoint(Point2D point, double angle, double distance) {     
    return new Vector2D((float)(point.getX() + distance*Math.cos(angle)), (float)(point.getY() + distance*Math.sin(angle)));        
  }
  
  public Vector2D movePointAboutArc( Point2D arcCenter, double angle) {
    return movePointAboutArc(this, arcCenter, angle);
  }
  
  public static Vector2D movePointAboutArc(Point2D point, Point2D arcCenter, double angle) {
    double startAngle = getAngle(arcCenter, point);
    return movePoint(arcCenter, (float)(startAngle - angle), arcCenter.distance(point));
  }

  public float getAngle(Point2D point2) {
    return getAngle(this, point2);
  }
  
  public static float getAngle(Point2D point1, Point2D point2) {
    return (float)Math.atan2(point2.getY() - point1.getY(),
        point2.getX() - point1.getX());
  }
  
  public Vector2D add(Vector2D addend) {
    return new Vector2D(this.x + addend.x, this.y + addend.y);
  }
  
  public Vector2D sub(Vector2D subtractor) {
    return new Vector2D(this.x - subtractor.x, this.y - subtractor.y);
  }
  
  public Vector2D div(float divisor) {
    return new Vector2D(this.x / divisor, this.y / divisor);
  }
  
  public Vector2D tim(Vector2D multiplier) {
    return new Vector2D(this.x * multiplier.x, this.y * multiplier.y);
  }

  public Vector2D tim(float multiplier) {
    return new Vector2D(this.x * multiplier, this.y * multiplier);
  }
  
  public float [] toFloatArray() {
    return new float []{ this.x, this.y};
  }
  
  public static float [][] toFloatArray(Vector2D[] vectorArray){
    float [][] floatArray = new float [vectorArray.length][2];
    for(int i = 0; i < vectorArray.length; i++) {
      floatArray[i] = vectorArray[i].toFloatArray();
    }
    return floatArray;
  }

  public static Vector2D fromFloatArray(float [] floatArray) {
    return new Vector2D(floatArray[0], floatArray[1]);
    
  }
  
  
  public static Vector2D[] fromFloatArray(float [][] floatArray) {
    Vector2D[] vectorArray = new Vector2D [floatArray.length];
    for(int i = 0; i < floatArray.length; i++) {
      vectorArray[i] = fromFloatArray(floatArray[i]);
    }
    return vectorArray;
    
  }
  
  /**
   * Returns the minimum abcissa of the vertices of <code>piece</code>.
   */
  public static float getMinX(Vector2D [] points) {
    float minX = java.lang.Float.POSITIVE_INFINITY;
    for (Vector2D point : points) {
      minX = Math.min(minX, point.x);
    }
    return minX;
  }

  /**
   * Returns the maximum abcissa of the vertices of <code>piece</code>.
   */
  public static float getMaxX(Vector2D [] points) {
    float maxX = java.lang.Float.NEGATIVE_INFINITY;
    for (Vector2D point : points) {
      maxX = Math.max(maxX, point.x);
    }
    return maxX;
  }

  /**
   * Returns the minimum ordinate of the vertices of <code>piece</code>.
   */
  public static float getMinY(Vector2D [] points) {
    float minY = java.lang.Float.POSITIVE_INFINITY;
    for (Vector2D point : points) {
      minY = Math.min(minY, point.x);
    }
    return minY;
  }

  /**
   * Returns the maximum ordinate of the vertices of <code>piece</code>.
   */
  public static float getMaxY(Vector2D [] points) {
    float maxY = java.lang.Float.NEGATIVE_INFINITY;
    for (Vector2D point : points) {
      maxY = Math.max(maxY, point.x);
    }
    return maxY;
  }

  /**
   * Returns a point which lies in the middle between this point and the
   * specified coordinates.
   * @param x the X coordinate of the second endpoint
   * @param y the Y coordinate of the second endpoint
   * @return the point in the middle
   * @since JavaFX 8.0
   */
  public Vector2D midpoint(double x, double y) {
      return new Vector2D(x + (getX() - x) / 2.0, y + (getY() - y) / 2.0);
  }

  /**
   * Returns a point which lies in the middle between this point and the
   * specified point.
   * @param point the other endpoint
   * @return the point in the middle
   * @throws NullPointerException if the specified {@code point} is null
   * @since JavaFX 8.0
   */
  public Vector2D midpoint(Point2D point) {
      return new Vector2D(point.getX(), point.getY());
  }
  
  public Vector2D clone() {
    return new Vector2D(this);
  }

}
