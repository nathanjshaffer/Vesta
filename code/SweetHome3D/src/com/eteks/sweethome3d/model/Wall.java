/*
 * Wall.java 3 juin 2006
 *
 * Sweet Home 3D, Copyright (c) 2006 Emmanuel PUYBARET / eTeks <info@eteks.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.eteks.sweethome3d.model;

import com.eteks.sweethome3d.model.Home;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.eteks.sweethome3d.model.WallPoint.PointState;
import com.eteks.sweethome3d.model.WallSide;
import com.eteks.sweethome3d.model.Wall.Property;
import com.eteks.sweethome3d.model.WallSide.WallSideState;
import com.eteks.sweethome3d.model.Line2D;
import com.eteks.sweethome3d.model.Vector2D;



//import com.eteks.sweethome3d.viewcontroller.PlanController.JoinedWall;

/**
 * A wall of a home plan.
 * @author Emmanuel Puybaret
 */
public class Wall extends HomeObject implements Selectable, Elevatable {
  /**
   * The properties of a wall that may change. <code>PropertyChangeListener</code>s added
   * to a wall will be notified under a property name equal to the string value of one these properties.
   */
//  public enum Property {X_START, Y_START, X_END, Y_END, ARC_EXTENT, WALL_AT_START, WALL_AT_END,
//                        THICKNESS, HEIGHT, HEIGHT_AT_END,
//                        LEFT_SIDE_COLOR, LEFT_SIDE_TEXTURE, LEFT_SIDE_SHININESS, LEFT_SIDE_BASEBOARD,
//                        RIGHT_SIDE_COLOR, RIGHT_SIDE_TEXTURE, RIGHT_SIDE_SHININESS, RIGHT_SIDE_BASEBOARD,
//                        PATTERN, TOP_COLOR, LEVEL}
  public enum Property {X_START, Y_START, X_END, Y_END, ARC_EXTENT, START_WALL_POINT, WALL_END_POINT,
    THICKNESS, HEIGHT, HEIGHT_AT_END,
    LEFT_SIDE_COLOR, LEFT_SIDE_TEXTURE, LEFT_SIDE_SHININESS, LEFT_SIDE_BASEBOARD,
    RIGHT_SIDE_COLOR, RIGHT_SIDE_TEXTURE, RIGHT_SIDE_SHININESS, RIGHT_SIDE_BASEBOARD,
    PATTERN, TOP_COLOR, LEVEL}

  private static final long serialVersionUID = 1L;

  private WallPoint           start;
  private WallPoint           end;
  private Float               arcExtent;
  private float               thickness;
  private Float               height;
  private Float               heightAtEnd;
  private Integer             leftSideColor;
  private HomeTexture         leftSideTexture;
  private float               leftSideShininess;
  private Baseboard           leftSideBaseboard;
  private Integer             rightSideColor;
  private HomeTexture         rightSideTexture;
  private float               rightSideShininess;
  private Baseboard           rightSideBaseboard;
  private boolean             symmetric = true;
  private TextureImage        pattern;
  private Integer             topColor;
  private Level               level;

  private transient Shape      shapeCache;
  private transient Vector2D arcCircleCenterCache;
  private transient Vector2D[] pointsCache;
  private transient Vector2D[] pointsIncludingBaseboardsCache;
  private WallSide []          wallSides;


  /**
   * 
   * @param id
   * @param start
   * @param end
   * @param thickness
   * @param height
   * @param pattern
   */
  
  public Wall(String id, WallPoint start, WallPoint end, float thickness, float height, TextureImage pattern) {
    super(id);
    this.setStartPoint(start);
    this.setEndPoint(end);
    this.thickness = thickness;
    this.height = height;
    this.pattern = pattern;
    this.wallSides = new WallSide[2];
    this.wallSides[WallSide.LEFT_SIDE] = new WallSide(this, WallSide.LEFT_SIDE);
    this.wallSides[WallSide.RIGHT_SIDE] = new WallSide(this, WallSide.RIGHT_SIDE);
  }
  

  /**
   * Creates a wall from (<code>xStart</code>,<code>yStart</code>)
   * to (<code>xEnd</code>, <code>yEnd</code>),
   * with given thickness. Height, left and right colors are <code>null</code>.
   * specify a height with the {@linkplain #Wall(float, float, float, float, float, float) other constructor}.
   */
  public Wall(Vector2D Start, Vector2D End, float thickness, float height) {
    this(Start.x, Start.y, End.x, End.y, thickness, height);
  }
  
  /**
   * Creates a wall from (<code>xStart</code>,<code>yStart</code>)
   * to (<code>xEnd</code>, <code>yEnd</code>),
   * with given thickness. Height, left and right colors are <code>null</code>.
   * @deprecated specify a height with the {@linkplain #Wall(float, float, float, float, float, float) other constructor}.
   */
  public Wall(float xStart, float yStart, float xEnd, float yEnd, float thickness) {
    this(xStart, yStart, xEnd, yEnd, thickness, 0);
  }

  /**
   * Creates a wall from (<code>xStart</code>,<code>yStart</code>)
   * to (<code>xEnd</code>, <code>yEnd</code>),
   * with given thickness and height. Pattern, left and right colors are <code>null</code>.
   */
  public Wall(float xStart, float yStart, float xEnd, float yEnd, float thickness, float height) {
    this(xStart, yStart, xEnd, yEnd, thickness, height, null);
  }

 /**
   * Creates a wall from (<code>xStart</code>,<code>yStart</code>)
   * to (<code>xEnd</code>, <code>yEnd</code>),
   * with given thickness and height. Pattern, left and right colors are <code>null</code>.
   * @since 6.4
   */
  public Wall(String id, float xStart, float yStart, float xEnd, float yEnd, float thickness, float height) {
    this(id, xStart, yStart, xEnd, yEnd, thickness, height, null);
  }

  /**
   * Creates a wall from (<code>xStart</code>,<code>yStart</code>)
   * to (<code>xEnd</code>, <code>yEnd</code>),
   * with given thickness, height and pattern.
   * Colors are <code>null</code>.
   * @since 4.0
   */
  public Wall(float xStart, float yStart, float xEnd, float yEnd, float thickness, float height, TextureImage pattern) {
    this(createId("wall"), xStart, yStart, xEnd, yEnd, thickness, height, pattern);
  }

  /**
   * Creates a wall from (<code>xStart</code>,<code>yStart</code>)
   * to (<code>xEnd</code>, <code>yEnd</code>),
   * with given thickness, height and pattern.
   * Colors are <code>null</code>.
   * @since 6.4
   */
  public Wall(String id, float xStart, float yStart, float xEnd, float yEnd, float thickness, float height, TextureImage pattern) {
    super(id);

    this.setStartPoint(new WallPoint(xStart, yStart));
    this.setEndPoint(new WallPoint(xEnd, yEnd));
    this.thickness = thickness;
    this.height = height;
    this.pattern = pattern;
    this.wallSides = new WallSide[2];
    this.wallSides[WallSide.LEFT_SIDE] = new WallSide(this, WallSide.LEFT_SIDE);
    this.wallSides[WallSide.RIGHT_SIDE] = new WallSide(this, WallSide.RIGHT_SIDE);
    System.out.println(id);
  }

  public void setStartPoint(WallPoint startPoint)
  {
    start = startPoint;
    start.addWall(this);
  }

  public void setEndPoint(WallPoint endPoint)
  {
    end = endPoint;
    end.addWall(this);
  }

  public WallPoint getStartPoint()
  {
    return start;
  }

  public WallPoint getEndPoint()
  {
    return end;
  }
  
  public boolean isStartPoint(WallPoint point) {
    return point == start;
  }
  
  /**
   * Returns the start point abscissa of this wall.
   */
  public float getXStart() {
    return this.start.getX();
  }

  /**
   * Sets the start point abscissa of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setXStart(float xStart) {
    if (xStart != this.start.getX()) {
      this.start.setX(xStart);
    }
  }

  /**
   * Returns the start point ordinate of this wall.
   */
  public float getYStart() {
    return this.start.getY();
  }

  /**
   * Sets the start point ordinate of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setYStart(float yStart) {
    if (yStart != this.start.getY()) {
      this.start.setY(yStart);
    }
  }
  
  public void setStartLocation(float x, float y) {
    this.start.setLocation(x, y);
  }

  /**
   * Returns the end point abscissa of this wall.
   */
  public float getXEnd() {
    return this.end.getX();
  }

  /**
   * Sets the end point abscissa of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setXEnd(float xEnd) {
    if (xEnd != this.end.getX()) {
      this.end.setX(xEnd);
    }
  }

  /**
   * Returns the end point ordinate of this wall.
   */
  public float getYEnd() {
    return this.end.getY();
  }

  /**
   * Sets the end point ordinate of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setYEnd(float yEnd) {
    if (yEnd != this.end.getY()) {
      this.end.setY(yEnd);
    }
  }
  
  public void setEndLocation(float x, float y) {
    this.end.setLocation(x, y);
  }

  /**
   * Returns the length of this wall.
   * @since 2.0
   */
  public float getLength() {
    if (this.arcExtent == null
        || this.arcExtent.floatValue() == 0) {
      return (float)Point2D.distance(this.start.getX(), this.start.getY(), this.end.getX(), this.end.getY());
    } else {
      Vector2D arcCircleCenter = getArcCircleCenter();
      float arcCircleRadius = (float)this.start.toVector().distance(arcCircleCenter);
      return Math.abs(this.arcExtent) * arcCircleRadius;
    }
  }

  /**
   * Returns the distance from the start point of this wall to its end point.
   * @since 3.0
   */
  public float getStartPointToEndPointDistance() {
    return (float)Point2D.distance(this.start.getX(), this.start.getY(), this.end.getX(), this.end.getY());
  }

  /**
   * Sets the arc extent of a round wall.
   * @since 3.0
   */
  public void setArcExtent(Float arcExtent) {
    if (arcExtent != this.arcExtent
        && (arcExtent == null || !arcExtent.equals(this.arcExtent))) {
      Float oldArcExtent = this.arcExtent;
      this.arcExtent = arcExtent;
      clearPointsCache();
      this.arcCircleCenterCache = null;
      firePropertyChange(Property.ARC_EXTENT.name(), oldArcExtent, arcExtent);
    }
  }

  /**
   * Returns the arc extent of a round wall or <code>null</code> if this wall isn't round.
   * @since 3.0
   */
  public Float getArcExtent() {
    return this.arcExtent;
  }

  /**
   * Returns the abscissa of the arc circle center of this wall.
   * If the wall isn't round, the return abscissa is at the middle of the wall.
   * @since 3.0
   */
  public float getXArcCircleCenter() {
    if (this.arcExtent == null) {
      return (this.start.getX() + this.end.getX()) / 2;
    } else {
      return (float)getArcCircleCenter().getX();
    }
  }

  /**
   * Returns the ordinate of the arc circle center of this wall.
   * If the wall isn't round, the return ordinate is at the middle of the wall.
   * @since 3.0
   */
  public float getYArcCircleCenter() {
    if (this.arcExtent == null) {
      return (this.start.getY() + this.end.getY()) / 2;
    } else {
      return (float)getArcCircleCenter().getY();
    }
  }

  /**
   * Returns the coordinates of the arc circle center of this wall.
   */
  public Vector2D getArcCircleCenter() {
    if (this.arcCircleCenterCache == null) {
      double startToEndPointsDistance = this.start.toVector().distanceSq(this.end.toVector());
      double wallToStartPointArcCircleCenterAngle = Math.abs(this.arcExtent) > Math.PI
          ? -(Math.PI + this.arcExtent) / 2
          : (Math.PI - this.arcExtent) / 2;
      float arcCircleCenterToWallDistance = -(float)(Math.tan(wallToStartPointArcCircleCenterAngle)
          * startToEndPointsDistance / 2);
      float xMiddlePoint = (this.start.getX() + this.end.getX()) / 2;
      float yMiddlePoint = (this.start.getY() + this.end.getY()) / 2;
      double angle = Math.atan2(this.start.getX() - this.end.getX(), this.end.getY() - this.start.getY());
      this.arcCircleCenterCache = new Vector2D(
          (float)(xMiddlePoint + arcCircleCenterToWallDistance * Math.cos(angle)),
          (float)(yMiddlePoint + arcCircleCenterToWallDistance * Math.sin(angle)));
    }
    return this.arcCircleCenterCache;
  }  

  /**
   * Returns the walls joined to this wall at end point.
   */
  public ArrayList<Wall> getWallsAtStart() {
    ArrayList<Wall> walls = (ArrayList<Wall>)this.start.joinedWalls.clone();
    walls.remove(this);
    return walls;
  }
  
  public boolean startIsJoined() {
    return this.start.joinedWalls.size() > 1;
  }

  
  public boolean endIsJoined() {
    return this.end.joinedWalls.size() > 1;
  }

  /**
   * Adds wall joined to this wall's start point
   * @param wallPoint
   */
  
  public void addWallAtStart(WallPoint wallPoint) {
    if (start != wallPoint) {
      this.start.combineWallPoints(wallPoint);
      
      clearPointsCache();
      firePropertyChange(Property.WALL_END_POINT.name(), null, wallPoint);
    }
  }

  /**
   * Returns the walls joined to this wall at end point.
   * @return
   */
  
  public ArrayList<Wall> getWallsAtEnd() {
    ArrayList<Wall> walls = (ArrayList<Wall>)this.end.joinedWalls.clone();
    walls.remove(this);
    return walls;
  }

  /**
   * Sets the wall joined to this wall's end point 
   */
  public void addWallAtEnd(WallPoint wallPoint) {
    if (end != wallPoint) {
      this.end.combineWallPoints(wallPoint);
      clearPointsCache();
      firePropertyChange(Property.WALL_END_POINT.name(), null, wallPoint);
    }
  }

  /**
   * Detaches <code>joinedWall</code> from this wall.
   */
  public void detachJoinedWall(WallPoint wallPoint) {
    // Detach the previously attached wall
    if (wallPoint != null) {
      if(wallPoint.joinedWalls.contains(this) && wallPoint.joinedWalls.size() > 1)
        start.switchJoinedWall(this, null);
    }
  }

  /**
   * Returns the thickness of this wall.
   */
  public float getThickness() {
    return this.thickness;
  }

  /**
   * Sets wall thickness. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setThickness(float thickness) {
    if (thickness != this.thickness) {
      float oldThickness = this.thickness;
      this.thickness = thickness;
      clearPointsCache();
      firePropertyChange(Property.THICKNESS.name(), oldThickness, thickness);
    }
  }

  /**
   * Returns the height of this wall. If {@link #getHeightAtEnd() getHeightAtEnd}
   * returns a value not <code>null</code>, the returned height should be
   * considered as the height of this wall at its start point.
   */
  public Float getHeight() {
    return this.height;
  }

  /**
   * Sets the height of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setHeight(Float height) {
    if (height != this.height
        && (height == null || !height.equals(this.height))) {
      Float oldHeight = this.height;
      this.height = height;
      firePropertyChange(Property.HEIGHT.name(), oldHeight, height);
    }
  }

  /**
   * Returns the height of this wall at its end point.
   */
  public Float getHeightAtEnd() {
    return this.heightAtEnd;
  }

  /**
   * Sets the height of this wall at its end point. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setHeightAtEnd(Float heightAtEnd) {
    if (heightAtEnd != this.heightAtEnd
        && (heightAtEnd == null || !heightAtEnd.equals(this.heightAtEnd))) {
      Float oldHeightAtEnd = this.heightAtEnd;
      this.heightAtEnd = heightAtEnd;
      firePropertyChange(Property.HEIGHT_AT_END.name(), oldHeightAtEnd, heightAtEnd);
    }
  }

  /**
   * Returns <code>true</code> if the height of this wall is different
   * at its start and end points.
   */
  public boolean isTrapezoidal() {
    return this.height != null
        && this.heightAtEnd != null
        && !this.height.equals(this.heightAtEnd);
  }

  public WallSide getSide(int wallSide) {
    return wallSides[wallSide];
  }
  
  public Integer getColor(int wallSide) {
    return this.wallSides[wallSide].getColor();
  }


  /**
   * Returns the left side color of this wall.
   */
  public Integer getLeftSideColor() {
    return this.wallSides[WallSide.LEFT_SIDE].getColor();
  }
  
  /**
   * Returns the right side color of this wall.
   */
  public Integer getRightSideColor() {
    return this.wallSides[WallSide.RIGHT_SIDE].getColor();
  }

  
  public void setColor(int wallSide, Integer color) {
    wallSides[wallSide].setColor(color);
  }

  /**
   * Sets left side color of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setLeftSideColor(Integer leftSideColor) {
    Integer oldLeftSideColor = getColor(WallSide.LEFT_SIDE);
    if (leftSideColor != oldLeftSideColor
        && (leftSideColor == null || !leftSideColor.equals(oldLeftSideColor))) {
      setColor(WallSide.LEFT_SIDE, leftSideColor);
      firePropertyChange(Property.LEFT_SIDE_COLOR.name(), oldLeftSideColor, leftSideColor);
    }
  }


  /**
   * Sets right side color of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setRightSideColor(Integer rightSideColor) {
    if (rightSideColor != this.wallSides[WallSide.RIGHT_SIDE].getColor()
        && (rightSideColor == null || !rightSideColor.equals(this.wallSides[WallSide.RIGHT_SIDE].getColor()))) {
      Integer oldLeftSideColor = this.wallSides[WallSide.RIGHT_SIDE].getColor();
      setColor(WallSide.RIGHT_SIDE, rightSideColor);
      firePropertyChange(Property.RIGHT_SIDE_COLOR.name(), oldLeftSideColor, rightSideColor);
    }
  }


  /**
   * Returns the left side texture of this wall.
   */
  public HomeTexture getLeftSideTexture() {
    return this.wallSides[WallSide.LEFT_SIDE].getTexture();
  }

  /**
   * 
   * @param wallSide
   * @param texture
   */
  public void setTexture(int wallSide, HomeTexture texture) {
    wallSides[wallSide].setTexture(texture);
  }
  
  /**
   * Sets the left side texture of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setLeftSideTexture(HomeTexture leftSideTexture) {
    if (leftSideTexture != this.wallSides[WallSide.LEFT_SIDE].getTexture()
        && (leftSideTexture == null || !leftSideTexture.equals(this.wallSides[WallSide.LEFT_SIDE].getTexture()))) {
      HomeTexture oldLeftSideTexture = this.wallSides[WallSide.LEFT_SIDE].getTexture();
      setTexture(WallSide.LEFT_SIDE, leftSideTexture);
      firePropertyChange(Property.LEFT_SIDE_TEXTURE.name(), oldLeftSideTexture, leftSideTexture);
    }
  }

  /**
   * Returns the right side texture of this wall.
   */
  public HomeTexture getRightSideTexture() {
    return this.wallSides[WallSide.RIGHT_SIDE].getTexture();
  }

  /**
   * Sets the right side texture of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   */
  public void setRightSideTexture(HomeTexture rightSideTexture) {
    if (rightSideTexture != this.wallSides[WallSide.RIGHT_SIDE].getTexture()
        && (rightSideTexture == null || !rightSideTexture.equals(this.wallSides[WallSide.RIGHT_SIDE].getTexture()))) {
      HomeTexture oldLeftSideTexture = this.wallSides[WallSide.RIGHT_SIDE].getTexture();
      setTexture(WallSide.RIGHT_SIDE, rightSideTexture);
      firePropertyChange(Property.RIGHT_SIDE_TEXTURE.name(), oldLeftSideTexture, rightSideTexture);
    }
  }

  /**
   * 
   * @param wallSide
   * @param shininess
   */
  public void setShininess(int wallSide, float shininess) {
    wallSides[wallSide].setShininess(shininess);
  }

  /**
   * Returns the left side shininess of this wall.
   * @return a value between 0 (matt) and 1 (very shiny)
   * @since 3.0
   */
  public float getLeftSideShininess() {
    return this.wallSides[WallSide.LEFT_SIDE].getShininess();
  }

  /**
   * Sets the left side shininess of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   * @since 3.0
   */
  public void setLeftSideShininess(float leftSideShininess) {
    if (leftSideShininess != this.wallSides[WallSide.LEFT_SIDE].getShininess()) {
      float oldLeftSideShininess = this.wallSides[WallSide.LEFT_SIDE].getShininess();
      setShininess(WallSide.LEFT_SIDE, leftSideShininess);
      firePropertyChange(Property.LEFT_SIDE_SHININESS.name(), oldLeftSideShininess, leftSideShininess);
    }
  }

  /**
   * Returns the right side shininess of this wall.
   * @return a value between 0 (matt) and 1 (very shiny)
   * @since 3.0
   */
  public float getRightSideShininess() {
    return this.wallSides[WallSide.RIGHT_SIDE].getShininess();
  }

  /**
   * Sets the right side shininess of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   * @since 3.0
   */
  public void setRightSideShininess(float rightSideShininess) {
    if (rightSideShininess != this.wallSides[WallSide.RIGHT_SIDE].getShininess()) {
      float oldRightSideShininess = this.wallSides[WallSide.RIGHT_SIDE].getShininess();
      setShininess(WallSide.RIGHT_SIDE, rightSideShininess);
      firePropertyChange(Property.RIGHT_SIDE_SHININESS.name(), oldRightSideShininess, rightSideShininess);
    }
  }

  /**
   * 
   * @param wallSide
   * @param baseboard
   */
  public void setBaseboard(int wallSide, Baseboard baseboard) {
    wallSides[wallSide].setBaseboard(baseboard);
  }

  /**
   * Returns the left side baseboard of this wall.
   * @since 5.0
   */
  public Baseboard getLeftSideBaseboard() {
    return this.wallSides[WallSide.LEFT_SIDE].getBaseboard();
  }

  /**
   * Sets the left side baseboard of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   * @since 5.0
   */
  public void setLeftSideBaseboard(Baseboard leftSideBaseboard) {
    if (leftSideBaseboard != this.wallSides[WallSide.LEFT_SIDE].getBaseboard()
        && (leftSideBaseboard == null || !leftSideBaseboard.equals(this.wallSides[WallSide.LEFT_SIDE].getBaseboard()))) {
      Baseboard oldLeftSideBaseboard = this.wallSides[WallSide.LEFT_SIDE].getBaseboard();
      setBaseboard(WallSide.LEFT_SIDE, leftSideBaseboard);
      clearPointsCache();
      firePropertyChange(Property.LEFT_SIDE_BASEBOARD.name(), oldLeftSideBaseboard, leftSideBaseboard);
    }
  }

  /**
   * Returns the right side baseboard of this wall.
   * @since 5.0
   */
  public Baseboard getRightSideBaseboard() {
    return this.wallSides[WallSide.RIGHT_SIDE].getBaseboard();
  }

  /**
   * Sets the right side baseboard of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   * @since 5.0
   */
  public void setRightSideBaseboard(Baseboard rightSideBaseboard) {
    if (rightSideBaseboard != this.wallSides[WallSide.RIGHT_SIDE].getBaseboard()
        && (rightSideBaseboard == null || !rightSideBaseboard.equals(this.wallSides[WallSide.RIGHT_SIDE].getBaseboard()))) {
      Baseboard oldRightSideBaseboard = this.wallSides[WallSide.RIGHT_SIDE].getBaseboard();
      setBaseboard(WallSide.RIGHT_SIDE, rightSideBaseboard);
      clearPointsCache();
      firePropertyChange(Property.RIGHT_SIDE_BASEBOARD.name(), oldRightSideBaseboard, rightSideBaseboard);
    }
  }

  /**
   * Returns the pattern of this wall in the plan.
   * @since 3.3
   */
  public TextureImage getPattern() {
    return this.pattern;
  }

  /**
   * Sets the pattern of this wall in the plan, and notifies
   * listeners of this change.
   * @since 3.3
   */
  public void setPattern(TextureImage pattern) {
    if (this.pattern != pattern) {
      TextureImage oldPattern = this.pattern;
      this.pattern = pattern;
      firePropertyChange(Property.PATTERN.name(), oldPattern, pattern);
    }
  }

  /**
   * Returns the color of the top of this wall in the 3D view.
   * @since 4.0
   */
  public Integer getTopColor() {
    return this.topColor;
  }

  /**
   * Sets the color of the top of this wall in the 3D view, and notifies
   * listeners of this change.
   * @since 4.0
   */
  public void setTopColor(Integer topColor) {
    if (this.topColor != topColor
        && (topColor == null || !topColor.equals(this.topColor))) {
      Integer oldTopColor = this.topColor;
      this.topColor = topColor;
      firePropertyChange(Property.TOP_COLOR.name(), oldTopColor, topColor);
    }
  }

  /**
   * Returns the level which this wall belongs to.
   * @since 3.4
   */
  public Level getLevel() {
    return this.level;
  }

  /**
   * Sets the level of this wall. Once this wall is updated,
   * listeners added to this wall will receive a change notification.
   * @since 3.4
   */
  public void setLevel(Level level) {
    if (level != this.level) {
      Level oldLevel = this.level;
      this.level = level;
      firePropertyChange(Property.LEVEL.name(), oldLevel, level);
    }
  }

  /**
   * Returns <code>true</code> if this wall is at the given <code>level</code>
   * or at a level with the same elevation and a smaller elevation index
   * or if the elevation of its highest point is higher than <code>level</code> elevation.
   * @since 3.4
   */
  public boolean isAtLevel(Level level) {
    if (this.level == level) {
      return true;
    } else if (this.level != null && level != null) {
      float wallLevelElevation = this.level.getElevation();
      float levelElevation = level.getElevation();
      return wallLevelElevation == levelElevation
             && this.level.getElevationIndex() < level.getElevationIndex()
          || wallLevelElevation < levelElevation
             && wallLevelElevation + getWallMaximumHeight() > levelElevation;
    } else {
      return false;
    }
  }

  /**
   * Returns the maximum height of the given wall.
   */
  private float getWallMaximumHeight() {
    if (this.height == null) {
      // Shouldn't happen
      return 0;
    } else if (isTrapezoidal()) {
      return Math.max(this.height, this.heightAtEnd);
    } else {
      return this.height;
    }
  }

  /**
   * Clears the points cache of this wall and of the walls attached to it.
   */
  public void clearPointsCache() {
    this.shapeCache = null;
    this.pointsCache = null;
    this.pointsIncludingBaseboardsCache = null;
    for (int i = 0; i < this.start.joinedWalls.size(); i++) {
      this.start.joinedWalls.get(i).pointsCache = null;
      this.start.joinedWalls.get(i).pointsIncludingBaseboardsCache = null;
    }
    for (int i = 0; i < this.end.joinedWalls.size(); i++) {
      this.end.joinedWalls.get(i).pointsCache = null;
      this.end.joinedWalls.get(i).pointsIncludingBaseboardsCache = null;
    }
  }
  
  public void clearArcCircleCenterCache() {
    this.arcCircleCenterCache = null;
  }

  /**
   * Returns the points of each corner of a wall not including its baseboards.
   * @return an array of the (x,y) coordinates of the wall corners.
   *    For a straight wall, the points at index 0 and 3 indicates the start of the wall,
   *    while the points at index 1 and 2 indicates the end of the wall.
   */
  public Vector2D[] getPoints() {
    return getPoints(false);
  }

  /**
   * Returns the points of each corner of a wall possibly including its baseboards.
   * @since 5.0
   */
  public Vector2D[] getPoints(boolean includeBaseboards) {
    if (includeBaseboards
        && (this.wallSides[WallSide.LEFT_SIDE].getBaseboard() != null
            || this.wallSides[WallSide.RIGHT_SIDE].getBaseboard() != null)) {
      if (this.pointsIncludingBaseboardsCache == null) {
        this.pointsIncludingBaseboardsCache = getShapePoints(true);
      }
      return clonePoints(this.pointsIncludingBaseboardsCache);
    } else {
      if (this.pointsCache == null) {
        this.pointsCache = getShapePoints(false);
      }
      return clonePoints(this.pointsCache);
    }
  }

  /**
   * Return a clone of the given <code>points</code> array.
   */
  public Vector2D[] clonePoints(Vector2D[] points) {
    Vector2D[] clonedPoints = new Vector2D [points.length];
    for (int i = 0; i < points.length; i++) {
      clonedPoints [i] = points [i].clone();
    }
    return clonedPoints;
  }
  
  private static float [][] getStartLeftLine(float [][] wallPoints) {
    float [][] line = new float[2][2];
    line[0] = wallPoints[0];
    line[1] = wallPoints[1];
    
    return line;
  }
  
  public static float [][] getStartRightLine(float [][] wallPoints) {
    float [][] line = new float[2][2];
    line[0] = wallPoints[wallPoints.length - 1];
    line[1] = wallPoints[wallPoints.length - 2];
    
    return line;
  }

  public static float [][] getEndLeftLine(float [][] wallPoints) {
    float [][] line = new float[2][2];
    line[0] = wallPoints[wallPoints.length / 2 - 1];
    line[1] = wallPoints[wallPoints.length / 2 - 2];
    
    return line;
  }

  public static float [][] getEndRightLine(float [][] wallPoints) {
    float [][] line = new float[2][2];
    line[0] = wallPoints[wallPoints.length / 2];
    line[1] = wallPoints[wallPoints.length / 2 + 1];
    
    return line;
  }
  
  public float getAngle() {
    return this.getStartPoint().toVector().getAngle(this.getEndPoint().toVector());
  }
  
  public float getAngleDegrees(boolean fromStart) {   
        double angle;
        if(fromStart)
            angle = Math.atan2(end.getY() - start.getY(),
                                end.getX() - start.getX());
        else
            angle = Math.atan2(start.getY() - end.getY(),
                                start.getX() - end.getX());
            
        angle = Math.toDegrees(angle);
        angle = 360 + angle;
        angle = angle%360;
        return (float)angle;
  }
  
  
  
  public float getWallAngleRotated(float angleOfRot, boolean fromStart, boolean clockwise) {
    float angle = getAngleDegrees(fromStart);
    if(clockwise) {
      angle = angle - angleOfRot;
    } else {
      angle = angle + angleOfRot;
    }
      
    angle = 360 + angle;
    angle = angle%360;
    return angle;
  }

  public float getAngleToNearestJoinedWall(WallPoint point, boolean clockWise) {
    float angle = 0f;
    getNearestJoinedWall(point, clockWise, angle);
    return  angle;
  }
  
  
  public Wall getNearestJoinedWall(WallPoint point, boolean clockWise, float angle) {
    ArrayList<Wall> walls = (ArrayList<Wall>)point.joinedWalls.clone();
    walls.remove(walls.indexOf(this));
    float thisWallAngle = getAngleDegrees(true);
    Wall tempWall, nearestWall  =null;
    float smallestAngle = 360;
    
    for(int i = 0; i < walls.size(); i++) {
      tempWall = walls.get(i);
      angle = tempWall.getWallAngleRotated(thisWallAngle, tempWall.isStartPoint(point), true);
      if(360 - angle < smallestAngle) {
        smallestAngle = 369 - angle;
        nearestWall = tempWall;
        clockWise = false;
      }
      if(angle < smallestAngle) {
        smallestAngle = angle;
        nearestWall = tempWall;
        clockWise = true;
      }
      angle = smallestAngle;
    }
    return nearestWall;
  }
  

  /**
   * Returns the points of the wall possibly including baseboards thickness.
   */
  private Vector2D[] getShapePoints(boolean includeBaseboards) {
    final float epsilon = 0.01f;
    ArrayList<Vector2D> wallPoints = new ArrayList<Vector2D>();
    wallPoints.addAll(Arrays.asList(this.getSide(0).getShapePoints(includeBaseboards)));
    wallPoints.addAll(Arrays.asList(this.getSide(1).getShapePoints(includeBaseboards)));

    return wallPoints.toArray(new Vector2D[wallPoints.size()]);
  }

  /**
   * Computes the rectangle or the circle arc of a wall according to its thickness
   * and possibly the thickness of its baseboards.
   */
  private Vector2D[] getUnjoinedShapePoints(boolean includeBaseboards) {

    ArrayList<Vector2D> wallPoints = new ArrayList<Vector2D>();
    wallPoints.addAll(Arrays.asList(this.getSide(0).getUnjoinedShapePoints(includeBaseboards)));
    wallPoints.addAll(Arrays.asList(this.getSide(1).getUnjoinedShapePoints(includeBaseboards)));

    return wallPoints.toArray(new Vector2D[wallPoints.size()]);
  }

  /**
   * Computes the exterior and interior arc points of a round wall at the given <code>index</code>.
   */
  private void computeRoundWallShapePoint(List<float []> wallPoints, float angle, int index, float angleDelta,
                                          float [] arcCircleCenter, float exteriorArcRadius, float interiorArcRadius) {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    float [] interiorArcPoint = new float [] {(float)(arcCircleCenter [0] + interiorArcRadius * cos),
                                              (float)(arcCircleCenter [1] - interiorArcRadius * sin)};
    float [] exteriorArcPoint = new float [] {(float)(arcCircleCenter [0] + exteriorArcRadius * cos),
                                              (float)(arcCircleCenter [1] - exteriorArcRadius * sin)};
    if (angleDelta > 0) {
      wallPoints.add(index, interiorArcPoint);
      wallPoints.add(wallPoints.size() - 1 - index, exteriorArcPoint);
    } else {
      wallPoints.add(index, exteriorArcPoint);
      wallPoints.add(wallPoints.size() - 1 - index, interiorArcPoint);
    }
  }
  
  /**
   * 
   * @param wall2
   * @return
   */
  public Vector2D getWallIntersectionPoint(Wall wall2) {
    return this.toLine().intersection(wall2.toLine());
  }
  
  public WallPoint getClosestEndpoint(Point2D point) {
    if(start.toVector().distanceSq(point ) <
    end.toVector().distanceSq(point)) {
      return start;
    }
    else return end;
  }
  
  

  /**
   * Compute the intersection between the line that joins <code>point1</code> to <code>point2</code>
   * and the line that joins <code>point3</code> and <code>point4</code>, and stores the result
   * in <code>point1</code>.
   */
  private void computeIntersectionWithLimit(float [] point1, float [] point2,
                                            float [] point3, float [] point4, float limit) {
    Vector2D intersection = Line2D.intersection(point1, point2,
    point3, point4);
    if (intersection.distanceSq(point1 [0], point1 [1]) < limit * limit) {
      point1 [0] = intersection.x;
      point1 [1] = intersection.y;
    }
   }

  /**
   * Returns <code>true</code> if this wall intersects
   * with the horizontal rectangle which opposite corners are at points
   * (<code>x0</code>, <code>y0</code>) and (<code>x1</code>, <code>y1</code>).
   */
  public boolean intersectsRectangle(float x0, float y0, float x1, float y1) {
    Rectangle2D rectangle = new Rectangle2D.Float(x0, y0, 0, 0);
    rectangle.add(x1, y1);
    return getShape(false).intersects(rectangle);
  }

  /**
   * Returns <code>true</code> if this wall contains the point at (<code>x</code>, <code>y</code>)
   * not including its baseboards, with a given <code>margin</code>.
   */
  public boolean containsPoint(float x, float y, float margin) {
    return containsPoint(x, y, false, margin);
  }

  /**
   * Returns <code>true</code> if this wall contains the point at (<code>x</code>, <code>y</code>)
   * possibly including its baseboards, with a given <code>margin</code>.
   * @since 5.0
   */
  public boolean containsPoint(float x, float y, boolean includeBaseboards, float margin) {
    return containsShapeAtWithMargin(getShape(includeBaseboards), x, y, margin);
  }

  /**
   * Returns <code>true</code> if the middle point of this wall is the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code>.
   */
  public boolean isMiddlePointAt(float x, float y, float margin) {
    Vector2D[] wallPoints = getPoints();
    int leftSideMiddlePointIndex = wallPoints.length / 4;
    int rightSideMiddlePointIndex = wallPoints.length - 1 - leftSideMiddlePointIndex;
    Line2D middleLine = wallPoints.length % 4 == 0
        ? new Line2D(wallPoints [leftSideMiddlePointIndex -1].midpoint( wallPoints [leftSideMiddlePointIndex]),
            wallPoints[rightSideMiddlePointIndex].midpoint(wallPoints[rightSideMiddlePointIndex + 1]))
        : new Line2D(wallPoints [leftSideMiddlePointIndex], wallPoints [rightSideMiddlePointIndex]);
    return containsShapeAtWithMargin(middleLine, x, y, margin);
  }

  /**
   * Returns <code>true</code> if this wall start line contains
   * the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code> around the wall start line.
   */
  public boolean containsWallStartAt(float x, float y, float margin) {
    Vector2D[] wallPoints = getPoints();
    Line2D startLine = new Line2D(wallPoints [0], wallPoints [wallPoints.length - 1]);
    return containsShapeAtWithMargin(startLine, x, y, margin);
  }

  /**
   * Returns <code>true</code> if this wall end line contains
   * the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code> around the wall end line.
   */
  public boolean containsWallEndAt(float x, float y, float margin) {
    Vector2D[] wallPoints = getPoints();
    Line2D endLine = new Line2D(wallPoints [wallPoints.length / 2 - 1], wallPoints [wallPoints.length / 2]);
    return containsShapeAtWithMargin(endLine, x, y, margin);
  }

  /**
   * Returns <code>true</code> if <code>shape</code> contains
   * the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code>.
   */
  private boolean containsShapeAtWithMargin(Shape shape, float x, float y, float margin) {
    if (margin == 0) {
      return shape.contains(x, y);
    } else {
      return shape.intersects(x - margin, y - margin, 2 * margin, 2 * margin);
    }
  }

  /**
   * Returns the shape matching this wall.
   */
  public Shape getShape(boolean includeBaseboards) {
    if (this.shapeCache == null) {
      Vector2D[] wallPoints = getPoints(includeBaseboards);
      GeneralPath wallPath = new GeneralPath();
      wallPath.moveTo(wallPoints [0].getX(), wallPoints [0].getY());
      for (int i = 1; i < wallPoints.length; i++) {
        wallPath.lineTo(wallPoints [i].getX(), wallPoints [i].getY());
      }
      wallPath.closePath();
      this.shapeCache = wallPath;
    }
    return this.shapeCache;
  }
  
  public Line2D toLine() {
    return new Line2D(start.toVector(), end.toVector());
  }

  /**
   * Moves this wall of (<code>dx</code>, <code>dy</code>) units.
   */
  public void move(float dx, float dy) {
    setXStart(getXStart() + dx);
    setYStart(getYStart() + dy);
    setXEnd(getXEnd() + dx);
    setYEnd(getYEnd() + dy);
  }
  
  
  public Wall split(Vector2D splitPoint) {
    if(splitPoint == null) {
      splitPoint = new Vector2D();
      splitPoint.x = (getXStart() + getXEnd()) / 2;
      splitPoint.y = (getYStart() + getYEnd()) / 2;
    }
    Wall secondWall = new Wall(0f, 0f, 0f, 0f, this.thickness, this.height);
    secondWall.setLevel(this.getLevel());
    addWallAtEnd(secondWall.getEndPoint()); 
    this.end.switchJoinedWall(this, secondWall.getStartPoint());
    System.out.println("this wall:  start - " + this.start.joinedWalls.size() + " end -" + this.end.joinedWalls.size());
    System.out.println("joined wall:  start - " + secondWall.start.joinedWalls.size() + " end -" + secondWall.end.joinedWalls.size());
        
    this.end.setLocation(splitPoint);
    
    return secondWall;
  }

  /**
   * Returns a duplicate of the <code>walls</code> list. All existing walls
   * are copied and their wall at start and end point are set with copied
   * walls only if they belong to the returned list.
   * The id of duplicated walls are regenerated.
   * @since 6.4
   */
  public static List<Wall> duplicate(List<Wall> walls) {
    ArrayList<Wall> wallsCopy = new ArrayList<Wall>(walls.size());
    // Duplicate walls
    for (Wall wall : walls) {
      wallsCopy.add((Wall)wall.duplicate());
    }
    DuplicateWallPoints(wallsCopy, walls);
    return wallsCopy;
  }
  
  private static void DuplicateWallPoints(ArrayList<Wall> wallsCopy, List<Wall> walls) {
    // Update walls at start and end point in wallsCopy
    for (int i = 0; i < walls.size(); i++) {
      Wall wall = walls.get(i);
//      wallsCopy.get(i).setStartPoint(wall.getStartPoint().Clone(wallsCopy, walls));
//      wallsCopy.get(i).setEndPoint(wall.getEndPoint().Clone(wallsCopy, walls));
      
    }
  }
  
  

  /**
   * Returns a clone of the <code>walls</code> list. All existing walls
   * are copied and their wall at start and end point are set with copied
   * walls only if they belong to the returned list.
   */
  public static List<Wall> clone(List<Wall> walls) {
    ArrayList<Wall> wallsCopy = new ArrayList<Wall>(walls.size());
    // Clone walls
    for (Wall wall : walls) {
      wallsCopy.add(wall.clone());
    }
    return wallsCopy;
  }

  /**
   * Returns a clone of this wall expected
   * its wall at start and wall at end aren't copied.
   */
  @Override
  public Wall clone() {
    Wall clone = (Wall)super.clone();
    clone.level = null;
    clone.shapeCache = null;
    clone.pointsCache = null;
    clone.pointsIncludingBaseboardsCache = null;
    return clone;
  }
  
  public WallState getState() {
    return new WallState(this);
  }

  /**
   * A wall side.
   */
 
  public static class WallState {
    public final Wall         wall;
    public final PointState   startPoint;
    public final PointState   endPoint;
    public final Level        level;
    public WallSideState []   wallSides;
    public final TextureImage pattern;
    public final Integer      topColor;
    public final Float        height;
    public final Float        heightAtEnd;
    public final float        thickness;
    public final Float        arcExtent;
    
    public WallState(Wall wall) {
      this.wall = wall;
      this.startPoint = wall.getStartPoint().getState();
      this.endPoint = wall.getEndPoint().getState();
      this.level = wall.getLevel();
      this.wallSides = new WallSideState[2];
      this.wallSides[WallSide.LEFT_SIDE] = wall.getSide(WallSide.LEFT_SIDE).getState();
      this.wallSides[WallSide.RIGHT_SIDE] = wall.getSide(WallSide.RIGHT_SIDE).getState();
      this.pattern = wall.getPattern();
      this.topColor = wall.getTopColor();
      this.height = wall.getHeight();
      this.heightAtEnd = wall.getHeightAtEnd();
      this.thickness = wall.getThickness();
      this.arcExtent = wall.getArcExtent();
    }
    
    public Wall getWall() {
      return this.wall;
    }
    
    public void resetWall() {
      resetJoinedWalls();
    }
    
    public Level getLevel() {
      return this.level;
    }
    
    /**
     * @return Returns the startPoint.
     */
    public PointState getStartPoint() {
      return this.startPoint;
    }

    /**
     * @return Returns the endPoint.
     */
    public PointState getEndPoint() {
      return this.endPoint;
    }

    /**
     * @return Returns the pattern.
     */
    public TextureImage getPattern() {
      return this.pattern;
    }


    /**
     * @return Returns the topColor.
     */
    public Integer getTopColor() {
      return this.topColor;
    }

    /**
     * @return Returns the height.
     */
    public Float getHeight() {
      return this.height;
    }

    /**
     * @return Returns the heightAtEnd.
     */
    public Float getHeightAtEnd() {
      return this.heightAtEnd;
    }

    /**
     * @return Returns the thickness.
     */
    public float getThickness() {
      return this.thickness;
    }

    /**
     * @return Returns the arcExtent.
     */
    public Float getArcExtent() {
      return this.arcExtent;
    }

    /**
     * A helper method that builds an array of <code>WallState2D</code> objects
     * for a given list of walls.
     */
    public static WallState [] getWallStates(List<Wall> walls) {
      WallState [] wallStates = new WallState [walls.size()];
      for (int i = 0; i < wallStates.length; i++) {
        wallStates [i] = new WallState(walls.get(i));
      }
      return wallStates;
    }
    
    /**
    * A helper method that builds a list of <code>Wall</code> objects
    * for a given array of <code>WallState</code> objects.
    */
   public static List<Wall> getWalls(WallState [] joinedWalls) {
     ArrayList<Wall> walls = new ArrayList<Wall>();
     for (int i = 0; i < joinedWalls.length; i++) {
       walls.add(joinedWalls [i].getWall());
     }
     return walls;
   }

    public void resetJoinedWalls() {
      wall.start.switchJoinedWall(wall, this.startPoint.point);
      wall.start.setLocation(wall.start.toVector());
      wall.end.switchJoinedWall(wall, this.endPoint.point);
      wall.end.setLocation(wall.end.toVector());
    }
    
  }
}

