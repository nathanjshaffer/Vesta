package com.eteks.sweethome3d.model;

import java.util.ArrayList;
import com.eteks.sweethome3d.model.Vector2D;
import com.eteks.sweethome3d.model.WallPoint;

public class Solver2D {
  
    //SolverNode root;

  public abstract class Constraint2D {
    static final int stepLimit = 20;
    boolean isModified;
  //  ArrayList<WallPoint> wallPoints;
  //  Vector2D [] newPositions;
    
    public SolverNode checkMove(WallPoint wallPoint, Vector2D position, int step) {
      return new SolverNode();
    }
    public abstract void calculateMoveEffect();
    public abstract boolean doCheckMove(WallPoint wallPoint, Vector2D position, int step);
    public abstract boolean CheckConstraint(WallPoint wallPoint, Vector2D position);
    
//    public class FixedPoint extends Constraint2D {
//      WallPoint constraintPoint;
//      float x;
//      float y;
//      public FixedPoint(WallPoint wallPoint) {
//        constraintPoint = wallPoint;
//        x = constraintPoint.getX();
//        y = constraintPoint.getY();
//      }
//
//      public SolverNode checkMove(WallPoint wallPoint, Vector2D position) {
//        
//      }
//
//      public boolean doCheckMove(WallPoint wallPoint, Vector2D position, int step) {
//        
//      }
//      
//      public boolean CheckConstraint(WallPoint wallPoint, Vector2D position) {
//        return wallPoint == constraintPoint && position.getX() == x && position.getY() == y;
//      }
//    }
//    
//    public class Horizontal extends Constraint2D {
//  
//      WallPoint constraintPoint1, constraintPoint2;
//      
//      Horizontal(WallPoint p1, WallPoint p2){
//        constraintPoint1 = p1;
//        constraintPoint2 = p2;
//      }
//      
//      public boolean doCheckMove(WallPoint wallPoint, Vector2D position, int step) {
//        
//      }
//    }
//  }
    
    
    
    class SolverNode {
      SolverNode parent;
      SolverNode child;
//      ArrayList<SolverNode> children = new ArrayList<SolverNode>();
//      ArrayList<WallPointLocation> locations = new ArrayList<WallPointLocation>();
      
//      public SolverNode addChild(SolverNode child) {
//        children.add(child);
//        return child;
//      }
//      
//      public SolverNode addChild() {
//        SolverNode child = new SolverNode();
//        children.add(child);
//        return child;
//      }
      
      public void setChild(SolverNode child) {
        this.child = child;
      }
      
      public void addWallPoint(WallPoint point, Vector2D value) {
//        locations.add(new WallPointLocation(point, value));
      }
      
      class WallPointLocation{
        
        public WallPointLocation(WallPoint point, Vector2D value) {
          this.wallPoint = point;
          this.value = value;
        }
        WallPoint wallPoint;
        Vector2D value;
      }
    }
  }
}
