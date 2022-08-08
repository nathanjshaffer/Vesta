package com.eteks.sweethome3d.model;

import java.awt.geom.Point2D;

import com.eteks.sweethome3d.model.Vector2D;


public class Line2D extends java.awt.geom.Line2D.Float {
  public Line2D(Vector2D point1, Vector2D point2) {
    super(point1, point2);
  }
  
  public Line2D(float x1, float y1, float x2, float y2) {
    super(x1, y1, x2, y2);
  }
  
  public float ptSegDist(Vector2D point1, Vector2D point2, double px, double py) {
    return (float)ptSegDist(point1.x, point1.y, point2.x, point2.y, px, py);
  }
  
  public float ptLineDistSq(Vector2D point1, Vector2D point2, double px, double py) {
    return (float)ptLineDistSq(point1.x, point1.y, point2.x, point2.y, px, py);
  }
  
//  public float getSlope() {
//    getP1();
//  }
  

  public Vector2D intersection(Line2D.Float line) {
    return intersection(this, line);
  }
  public static Vector2D intersection(Line2D.Float a, Line2D.Float b) {
    float x1 = (float)a.getX1(), y1 = (float)a.getY1(), x2 = (float)a.getX2(), y2 = (float)a.getY2(), x3 = (float)b.getX1(), y3 = (float)b.getY1(),
            x4 = (float)b.getX2(), y4 = (float)b.getY2();
    float d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
    if (d == 0) {
        return null;
    }

    float xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
    float yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

    return new Vector2D(xi, yi);
  }
    
  public static Vector2D intersection(float [] point1, float [] point2,
                                     float [] point3, float [] point4) {
    return intersection(new Line2D.Float(point1[0], point1[1], point2[0], point2[1])  , new Line2D.Float(point3[0], point3[1], point4[0], point4[1]) );
  }
  
  
  public static Vector2D intersection(Point2D.Float point1, Point2D.Float point2,
                                           Point2D.Float point3, Point2D.Float point4) {
    return intersection(new Line2D.Float(point1, point2)  , new Line2D.Float(point3, point4) );
  }
}
