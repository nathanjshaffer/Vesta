/*
 * DimensionLine.java 17 sept 2007
 *
 * Sweet Home 3D, Copyright (c) 2007 Emmanuel PUYBARET / eTeks <info@eteks.com>
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

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.eteks.sweethome3d.model.Vector2D;

/**
 * A dimension line in plan.
 * @author Emmanuel Puybaret
 */
public class DimensionLine extends HomeObject implements Selectable, Elevatable {
  /**
   * The properties of a dimension line that may change. <code>PropertyChangeListener</code>s added
   * to a dimension line will be notified under a property name equal to the string value of one these properties.
   */
  public enum Property {X_START, Y_START, X_END, Y_END, OFFSET, LENGTH_STYLE, LEVEL}

  private static final long serialVersionUID = 1L;

//  private float               start.x;
//  private float               start.y;
//  private float               end.x;
//  private float               end.y;
  private Vector2D            start;
  private Vector2D            end;
  private float               offset;
  private TextStyle           lengthStyle;
  private Level               level;

  private transient Shape shapeCache;

  /**
   * Creates a dimension line from (<code>start.x</code>,<code>start.y</code>)
   * to (<code>end.x</code>, <code>end.y</code>), with a given offset.
   */
  public DimensionLine(float xStart, float yStart, float xEnd, float yEnd, float offset) {
    this(createId("dimensionLine"), xStart, yStart, xEnd, yEnd, offset);
  }

  /**
   * Creates a dimension line from (<code>start.x</code>,<code>start.y</code>)
   * to (<code>end.x</code>, <code>end.y</code>), with a given offset.
   * @since 6.4
   */
  public DimensionLine(String id, float xStart, float yStart, float xEnd, float yEnd, float offset) {
    super(id);
    this.start = new Vector2D(xStart, yStart);
    this.end = new Vector2D(xEnd, yEnd);
    this.offset = offset;
  }

  /**
   * Returns the start point abscissa of this dimension line.
   */
  public float getXStart() {
    return this.start.x;
  }

  /**
   * Sets the start point abscissa of this dimension line. Once this dimension line
   * is updated, listeners added to this dimension line will receive a change notification.
   */
  public void setXStart(float x) {
    if (x != this.start.x) {
      float oldXStart = this.start.x;
      this.start.x = x;
      this.shapeCache = null;
      firePropertyChange(Property.X_START.name(), oldXStart, x);
    }
  }

  /**
   * Returns the start point ordinate of this dimension line.
   */
  public float getYStart() {
    return this.start.y;
  }

  /**
   * Sets the start point ordinate of this dimension line. Once this dimension line
   * is updated, listeners added to this dimension line will receive a change notification.
   */
  public void setYStart(float y) {
    if (y != this.start.y) {
      float oldYStart = this.start.y;
      this.start.y = y;
      this.shapeCache = null;
      firePropertyChange(Property.Y_START.name(), oldYStart, y);
    }
  }

  /**
   * Returns the end point abscissa of this dimension line.
   */
  public float getXEnd() {
    return this.end.x;
  }

  /**
   * Sets the end point abscissa of this dimension line. Once this dimension line
   * is updated, listeners added to this dimension line will receive a change notification.
   */
  public void setXEnd(float x) {
    if (x != this.end.x) {
      float oldXEnd = this.end.x;
      this.end.x = x;
      this.shapeCache = null;
      firePropertyChange(Property.X_END.name(), oldXEnd, x);
    }
  }

  /**
   * Returns the end point ordinate of this dimension line.
   */
  public float getYEnd() {
    return this.end.y;
  }

  /**
   * Sets the end point ordinate of this dimension line. Once this dimension line
   * is updated, listeners added to this dimension line will receive a change notification.
   */
  public void setYEnd(float y) {
    if (y != this.end.y) {
      float oldYEnd = this.end.y;
      this.end.y = y;
      this.shapeCache = null;
      firePropertyChange(Property.Y_END.name(), oldYEnd, y);
    }
  }

  /**
   * Returns the offset of this dimension line.
   */
  public float getOffset() {
    return this.offset;
  }

  /**
   * Sets the offset of this dimension line.  Once this dimension line
   * is updated, listeners added to this dimension line will receive a change notification.
   */
  public void setOffset(float offset) {
    if (offset != this.offset) {
      float oldOffset = this.offset;
      this.offset = offset;
      this.shapeCache = null;
      firePropertyChange(Property.OFFSET.name(), oldOffset, offset);
    }
  }

  /**
   * Returns the length of this dimension line.
   */
  public float getLength() {
    return (float)Point2D.distance(getXStart(), getYStart(), getXEnd(), getYEnd());
  }

  /**
   * Returns the text style used to display dimension line length.
   */
  public TextStyle getLengthStyle() {
    return this.lengthStyle;
  }

  /**
   * Sets the text style used to display dimension line length.
   * Once this dimension line is updated, listeners added to it will receive a change notification.
   */
  public void setLengthStyle(TextStyle lengthStyle) {
    if (lengthStyle != this.lengthStyle) {
      TextStyle oldLengthStyle = this.lengthStyle;
      this.lengthStyle = lengthStyle;
      firePropertyChange(Property.LENGTH_STYLE.name(), oldLengthStyle, lengthStyle);
    }
  }

  /**
   * Returns the level which this dimension line belongs to.
   * @since 3.4
   */
  public Level getLevel() {
    return this.level;
  }

  /**
   * Sets the level of this dimension line. Once this dimension line is updated,
   * listeners added to this dimension line will receive a change notification.
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
   * Returns <code>true</code> if this dimension line is at the given <code>level</code>
   * or at a level with the same elevation and a smaller elevation index.
   * @since 3.4
   */
  public boolean isAtLevel(Level level) {
    return this.level == level
        || this.level != null && level != null
           && this.level.getElevation() == level.getElevation()
           && this.level.getElevationIndex() < level.getElevationIndex();

  }

  /**
   * Returns the points of the rectangle surrounding
   * this dimension line and its extension lines.
   * @return an array of the 4 (x,y) coordinates of the rectangle.
   */
  public Vector2D[] getPoints() {
    double angle = Math.atan2(this.end.y - this.start.y, this.end.x - this.start.x);
    Vector2D d = new Vector2D();
    d.x = (float)-Math.sin(angle) * this.offset;
    d.y = (float)Math.cos(angle) * this.offset;

    return new Vector2D [] {this.start, this.start.add(d),
                            this.end.add(d), this.end};
  }

  /**
   * Returns <code>true</code> if this dimension line intersects
   * with the horizontal rectangle which opposite corners are at points
   * (<code>x0</code>, <code>y0</code>) and (<code>x1</code>, <code>y1</code>).
   */
  public boolean intersectsRectangle(float x0, float y0, float x1, float y1) {
    Rectangle2D rectangle = new Rectangle2D.Float(x0, y0, 0, 0);
    rectangle.add(x1, y1);
    return getShape().intersects(rectangle);
  }

  /**
   * Returns <code>true</code> if this dimension line contains
   * the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code>.
   */
  public boolean containsPoint(float x, float y, float margin) {
    return containsShapeAtWithMargin(getShape(), x, y, margin);
  }

  /**
   * Returns <code>true</code> if the middle point of this dimension line
   * is the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code>.
   */
  public boolean isMiddlePointAt(float x, float y, float margin) {
    double angle = Math.atan2(this.end.y - this.start.y, this.end.x - this.start.x);
    float dx = (float)-Math.sin(angle) * this.offset;
    float dy = (float)Math.cos(angle) * this.offset;
    float xMiddle = (this.start.x + this.end.x) / 2 + dx;
    float yMiddle = (this.start.y + this.end.y) / 2 + dy;
    return Math.abs(x - xMiddle) <= margin && Math.abs(y - yMiddle) <= margin;
  }

  /**
   * Returns <code>true</code> if the extension line at the start of this dimension line
   * contains the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code> around the extension line.
   */
  public boolean containsStartExtensionLinetAt(float x, float y, float margin) {
    double angle = Math.atan2(this.end.y - this.start.y, this.end.x - this.start.x);
    Line2D startExtensionLine = new Line2D.Float(this.start.x, this.start.y,
        this.start.x + (float)-Math.sin(angle) * this.offset,
        this.start.y + (float)Math.cos(angle) * this.offset);
    return containsShapeAtWithMargin(startExtensionLine, x, y, margin);
  }

  /**
   * Returns <code>true</code> if the extension line at the end of this dimension line
   * contains the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code> around the extension line.
   */
  public boolean containsEndExtensionLineAt(float x, float y, float margin) {
    double angle = Math.atan2(this.end.y - this.start.y, this.end.x - this.start.x);
    Line2D endExtensionLine = new Line2D.Float(this.end.x, this.end.y,
        this.end.x + (float)-Math.sin(angle) * this.offset,
        this.end.y + (float)Math.cos(angle) * this.offset);
    return containsShapeAtWithMargin(endExtensionLine, x, y, margin);
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
   * Returns the shape matching this dimension line.
   */
  private Shape getShape() {
    if (this.shapeCache == null) {
      // Create the rectangle that matches piece bounds
      double angle = Math.atan2(this.end.y - this.start.y, this.end.x - this.start.x);
      float dx = (float)-Math.sin(angle) * this.offset;
      float dy = (float)Math.cos(angle) * this.offset;

      GeneralPath dimensionLineShape = new GeneralPath();
      // Append dimension line
      dimensionLineShape.append(new Line2D.Float(this.start.x + dx, this.start.y + dy, this.end.x + dx, this.end.y + dy), false);
      // Append extension lines
      dimensionLineShape.append(new Line2D.Float(this.start.x, this.start.y, this.start.x + dx, this.start.y + dy), false);
      dimensionLineShape.append(new Line2D.Float(this.end.x, this.end.y, this.end.x + dx, this.end.y + dy), false);
      // Cache shape
      this.shapeCache = dimensionLineShape;
    }
    return this.shapeCache;
  }

  /**
   * Moves this dimension line of (<code>dx</code>, <code>dy</code>) units.
   */
  public void move(float dx, float dy) {
    setXStart(getXStart() + dx);
    setYStart(getYStart() + dy);
    setXEnd(getXEnd() + dx);
    setYEnd(getYEnd() + dy);
  }

  /**
   * Returns a clone of this dimension line.
   */
  @Override
  public DimensionLine clone() {
    DimensionLine clone = (DimensionLine)super.clone();
    clone.level = null;
    return clone;
  }
}
